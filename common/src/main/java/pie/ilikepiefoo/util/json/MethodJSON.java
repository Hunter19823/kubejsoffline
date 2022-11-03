package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;

public class MethodJSON {
	public static JsonObject of(Method method) {
		JsonObject object = new JsonObject();
		// Name of the method
		var temp = SafeOperations.safeUnwrapName(method);
		if(temp != null)
			object.addProperty("name", temp);

		// Return type of the method
		Optional<?> type = SafeOperations.tryGetFirst(
				method::getGenericReturnType,
				method::getReturnType
		);
		if(type.isPresent())
			object.add("type", TypeJSON.of((Type) type.get()));
		else{
			return new JsonObject();
		}

		// Attach Parameters, annotations, and modifiers.
		ExecutableJSON.attach(object, method);

		return object;
	}

	public static JsonArray of(Method[] methods) {
		if(methods == null)
			return new JsonArray();
		JsonArray object = new JsonArray();
		for(var method : methods)
			object.add(of(method));
		return object;
	}
}
