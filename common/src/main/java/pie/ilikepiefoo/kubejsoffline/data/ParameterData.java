package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import java.util.List;

public class ParameterData extends CommonData {

	public ParameterData( int modifier, @Nonnull String name, @Nonnull TypeData type ) {
		super(name, modifier);
		if (type == null) {
			throw new NullPointerException("Type cannot be null");
		}
		setType(type);
	}

	public static JsonElement toJSON(List<ParameterData> parameters) {
		JsonArray array = new JsonArray();
		for (ParameterData parameter : parameters) {
			array.add(parameter.toJSON());
		}
		return array;
	}

	@Override
	public JsonElement toJSON() {
		JsonObject object = super.toReference();
		object.addProperty(JSONProperty.MODIFIERS.jsName, getModifiers());
		object.addProperty(JSONProperty.PARAMETER_NAME.jsName, getName());
		object.add(JSONProperty.PARAMETER_TYPE.jsName, getType().toReference());
		if (!getAnnotations().isEmpty()) {
			object.add(JSONProperty.PARAMETER_ANNOTATIONS.jsName, AnnotationData.toJSON(getAnnotations()));
		}

		return object;
	}
}
