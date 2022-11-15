package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class TypeJSON {
	public static JsonObject of(Type type) {
		if(type == null)
			return new JsonObject();
		JsonObject object = ClassJSONManager.getInstance().getTypeData(type);
		if(object == null)
			return null;
		var typeName = SafeOperations.safeUnwrapName(type);
		if(typeName != null)
			object.addProperty("name", typeName);

		attachGenericAndArrayData(object, type);

		return object;
	}

	public static void attachGenericAndArrayData(JsonObject object, Supplier<Type> typeSupplier) {
		SafeOperations.tryGet(typeSupplier).ifPresent((type) -> attachGenericAndArrayData(object, type));
	}

	public static void attachGenericAndArrayData(JsonObject object, Type type) {
		int arrayDepth = 0;
		if(type instanceof Class<?> subject) {
			if(subject.isArray()) {
				while (subject.isArray()) {
					arrayDepth++;
					subject = subject.getComponentType();
				}
				attachGenericAndArrayData(object, subject);
			}
		} else if(type instanceof GenericArrayType genericArrayType) {
			var comp = SafeOperations.tryGet(genericArrayType::getGenericComponentType);
			if(comp.isPresent()){
				attachGenericAndArrayData(object, comp.get());
				arrayDepth++;
			}
		}
		if(type instanceof ParameterizedType parameterizedType) {
			addGenericData(object, parameterizedType::getActualTypeArguments);
		}


		if (arrayDepth > 0) {
			object.addProperty("arrayDepth", arrayDepth);
			object.addProperty("isArray", true);
		}
	}

	public static void addGenericData(JsonObject obj, Supplier<Type[]> typeArgs) {
		var arguments = new JsonArray();
		var typeArguments = SafeOperations.tryGet(typeArgs);
		if(typeArguments.isEmpty())
			return;
		for(var typeArgument : typeArguments.get()) {
			var argument = of(typeArgument);
			if(argument == null || !argument.has("id"))
				continue;
			if(argument.size() > 0)
				arguments.add(argument.get("id").getAsInt());
		}
		if(arguments.size() > 0)
			obj.add("args", arguments);
	}
}
