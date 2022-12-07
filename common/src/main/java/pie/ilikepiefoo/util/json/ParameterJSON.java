package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

public class ParameterJSON {
	@Nullable
	public static JsonObject of(@Nonnull Parameter parameter) {
		JsonObject object = new JsonObject();
		var temp = SafeOperations.safeUnwrapName(parameter);
		if(temp == null || temp.isBlank())
			return null;
		object.addProperty(JSONProperty.PARAMETER_NAME.jsName, temp);

		var type = TypeJSON.of(SafeOperations.safeUnwrapReturnType(parameter));
		if(type == null)
			return null;
		object.addProperty(JSONProperty.PARAMETER_TYPE.jsName, type.get(JSONProperty.TYPE_ID.jsName).getAsInt());

		TypeJSON.attachGenericAndArrayData(type, parameter::getType);
		TypeJSON.attachGenericAndArrayData(type, parameter::getParameterizedType);

		var annotations = AnnotationJSON.of(parameter);
		if(annotations.size() > 0)
			object.add(JSONProperty.PARAMETER_ANNOTATIONS.jsName, annotations);

		return object;
	}

	@Nullable
	public static JsonArray of(@Nonnull Executable executable) {
		JsonArray parameters = new JsonArray();
		var params = SafeOperations.tryGet(executable::getParameters);
		if(params.isEmpty())
			return null;
		for(var parameter : params.get()) {
			var parameterJson = of(parameter);
			if(parameterJson == null || parameterJson.size() == 0)
				return null;
			parameters.add(parameterJson);
		}
		return parameters;
	}
}
