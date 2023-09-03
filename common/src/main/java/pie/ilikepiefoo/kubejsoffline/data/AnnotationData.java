package pie.ilikepiefoo.kubejsoffline.data;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.util.List;

public class AnnotationData implements JSONLike {

	private final ClassData annotationType;
	private final Object value;

	public AnnotationData(ClassData annotationType, Object value) {
		this.annotationType = annotationType;
		this.value = value;
	}

	public static JsonElement toJSON(List<AnnotationData> annotations) {
		var jsonArray = new JsonArray();
		for (AnnotationData annotation : annotations) {
			jsonArray.add(annotation.toJSON());
		}
		return jsonArray;
	}

	@Override
	public JsonElement toJSON() {
		JsonObject object = new JsonObject();
		object.addProperty(JSONProperty.ANNOTATION_TYPE.jsName, annotationType.getId());
		object.addProperty(JSONProperty.ANNOTATION_STRING.jsName, getValue());
		return object;
	}

	public String getValue() {
		return "" + value;
	}

	public ClassData getAnnotationType() {
		return annotationType;
	}
}
