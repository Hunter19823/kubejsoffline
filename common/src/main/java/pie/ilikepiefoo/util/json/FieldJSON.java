package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

public class FieldJSON {
	public static JsonObject of(Field field) {
		JsonObject object = new JsonObject();
		Optional<?> type = SafeOperations.tryGetFirst(
				field::getGenericType,
				field::getType
		);
		if(type.isEmpty())
			return object;
		var typeJson = TypeJSON.of((Type) type.get());
		if(typeJson == null)
			return object;
		object.addProperty("type", typeJson.get("id").getAsInt());

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
