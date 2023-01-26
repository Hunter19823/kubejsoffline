package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class TypeJSON {
	@Nullable
	public static JsonObject of(@Nullable Type type) {
		if(type == null)
			return null;
		String name = SafeOperations.safeUnwrapName(type);
		if(name == null || name.isBlank())
			return null;

		JsonObject object = ClassJSONManager.getInstance().getTypeData(type);
		if(object == null)
			return null;
		object.addProperty(JSONProperty.BASE_CLASS_NAME.jsName, name);

		attachGenericAndArrayData(object, type);

		return object;
	}

	public static void attachGenericAndArrayData(@Nonnull JsonObject object, @Nullable Supplier<Type> typeSupplier) {
		SafeOperations.tryGet(typeSupplier).ifPresent((type) -> attachGenericAndArrayData(object, type));
	}

	public static void attachGenericAndArrayData(@Nonnull JsonObject object, @Nullable Type type) {
		if(type == null)
			return;
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
			object.addProperty(JSONProperty.ARRAY_DEPTH.jsName, arrayDepth);
		}
	}

	public static void addGenericData(@Nonnull JsonObject obj, @Nullable Supplier<Type[]> typeArgs) {
		var arguments = new JsonArray();
		var typeArguments = SafeOperations.tryGet(typeArgs);
		if(typeArguments.isEmpty())
			return;
		for(var typeArgument : typeArguments.get()) {
			var argument = of(typeArgument);
			if(argument == null)
				return;
			if(argument.size() > 0)
				arguments.add(argument.get(JSONProperty.TYPE_ID.jsName).getAsInt());
		}
		if(arguments.size() > 0)
			obj.add(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName, arguments);
	}
}
