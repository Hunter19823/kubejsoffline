package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class FieldJSON {
	@Nullable
	public static JsonObject of(@Nullable Field field) {
		var type = SafeOperations.safeUnwrapReturnType(field);
		if(type == null)
			return null;
		var typeJson = TypeJSON.of(type);
		if(typeJson == null || !typeJson.has(JSONProperty.TYPE_ID.jsName))
			return null;
		var object = new JsonObject();

		// Add the name of the field
		object.addProperty(JSONProperty.FIELD_NAME.jsName, CompressionJSON.getInstance().compress(SafeOperations.safeUnwrapName(field)));
		object.addProperty(JSONProperty.FIELD_TYPE.jsName, typeJson.get(JSONProperty.TYPE_ID.jsName).getAsInt());

		TypeJSON.attachGenericAndArrayData(typeJson, field::getType);
		TypeJSON.attachGenericAndArrayData(typeJson, field::getGenericType);

		// Annotations of the field.
		var annotations = AnnotationJSON.of(field);
		if(annotations.size() > 0)
			object.add(JSONProperty.FIELD_ANNOTATIONS.jsName, annotations);

		// Modifiers of the field
		object.addProperty(JSONProperty.MODIFIERS.jsName, field.getModifiers());


		return object;
	}

	@Nonnull
	public static JsonArray of(@Nullable Field[] fields) {
		if(fields == null)
			return new JsonArray();
		JsonArray array = new JsonArray();
		for(var field : fields) {
			var fieldJson = of(field);
			if(fieldJson != null)
				array.add(fieldJson);
		}
		return array;
	}
}
