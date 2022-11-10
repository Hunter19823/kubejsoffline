package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Field;

public class FieldJSON {
	public static JsonObject of(Field field) {
		JsonObject object = new JsonObject();
		var type = SafeOperations.safeUnwrapReturnType(field);
		if(type == null)
			return object;
		var typeJson = TypeJSON.of(type);
		if(typeJson == null)
			return object;
		// Add the name of the field
		object.addProperty("name", SafeOperations.safeUnwrapName(field));
		object.addProperty("type", typeJson.get("id").getAsInt());
		TypeJSON.attachGenericAndArrayData(typeJson, field::getType);
		TypeJSON.attachGenericAndArrayData(typeJson, field::getGenericType);

		// Annotations of the field.
		var annotations = AnnotationJSON.of(field);
		if(annotations.size() > 0)
			object.add("ano", annotations);

		// Modifiers of the field
		object.addProperty("mod", field.getModifiers());


		return object;
	}

	public static JsonArray of(Field[] fields) {
		if(fields == null)
			return new JsonArray();
		JsonArray array = new JsonArray();
		for(var field : fields)
			array.add(of(field));
		return array;
	}
}
