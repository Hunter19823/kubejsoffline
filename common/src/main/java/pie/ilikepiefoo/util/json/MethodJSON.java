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
		var type = SafeOperations.safeUnwrapReturnType(method);
		if(type != null) {
			var typeObject = TypeJSON.of(type);
			if(typeObject == null)
				return new JsonObject();
			TypeJSON.attachGenericAndArrayData(typeObject, method::getReturnType);
			TypeJSON.attachGenericAndArrayData(typeObject, method::getGenericReturnType);
			object.addProperty("type", typeObject.get("id").getAsInt());
		}else{
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
