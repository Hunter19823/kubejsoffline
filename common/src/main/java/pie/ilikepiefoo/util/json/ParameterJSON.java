package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class ParameterJSON {
	public static JsonObject of(Parameter parameter) {
		JsonObject object = new JsonObject();
		var temp = SafeOperations.safeUnwrapName(parameter);
		if(temp != null)
			object.addProperty("name", temp);
		temp = SafeOperations.safeUnwrapReturnType(parameter);
		if(temp != null)
			object.addProperty("type", temp);
		var annotations = AnnotationJSON.of(parameter);
		if(annotations.size() > 0)
			object.add("ano", annotations);
		return object;
	}

	public static JsonArray of(Executable executable) {
		JsonArray parameters = new JsonArray();
		for(var parameter : executable.getParameters())
			parameters.add(of(parameter));
		return parameters;
	}
}
