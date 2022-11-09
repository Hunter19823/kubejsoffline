package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeJSON {
	public static JsonObject of(Type type) {
		if(type == null)
			return new JsonObject();
		JsonObject object = ClassJSONManager.getInstance().getTypeData(type);
		if(object == null)
			return null;
		object.addProperty("name", SafeOperations.safeUnwrapName(type));
		object.addProperty("type", SafeOperations.safeUnwrapReturnType(type));
		if(type instanceof ParameterizedType parameterizedType) {
			var arguments = new JsonArray();
			var typeArguments = SafeOperations.tryGet(parameterizedType::getActualTypeArguments);
			if(typeArguments.isEmpty())
				return object;
			for(var typeArgument : typeArguments.get()) {
				var argument = of(typeArgument);
				if(argument == null)
					continue;
				if(argument.size() > 0)
					arguments.add(argument.get("id").getAsInt());
			}
			if(arguments.size() > 0)
				object.add("args", arguments);
		}
		if(type instanceof Class<?> clazz) {
			if(clazz.isArray()) {
				int arrayDepth = 0;
				while (clazz.isArray()) {
					arrayDepth++;
					clazz = clazz.getComponentType();
				}
				if (arrayDepth > 0) {
					object.addProperty("arrayDepth", arrayDepth);
					object.addProperty("isArray", true);
				}

				var component = of(clazz);
				if(component != null) {
					object.addProperty("componentType", component.get("id").getAsInt());
					object.addProperty("type", SafeOperations.safeUnwrapReturnType(clazz));
				}
			}
		}

		return object;
	}
}
