package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public interface JSONLike {
	static JsonArray toJSON( Iterable<?> list ) {
		JsonArray array = new JsonArray();
		for (var data : list) {
			if (data instanceof JSONLike jsonLike) {
				array.add(jsonLike.toJSON());
			} else if (data instanceof Number numberData) {
				array.add(numberData);
			} else if (data instanceof String stringData) {
				array.add(stringData);
			} else if (data instanceof Boolean boolData) {
				array.add(boolData);
			} else if (data instanceof Character charData) {
				array.add(charData);
			} else if (data instanceof JsonElement jsonElement) {
				array.add(jsonElement);
			} else {
				throw new IllegalArgumentException("Cannot convert " + data.getClass().getName() + " to JSON");
			}
		}
		return array;
	}

	JsonElement toJSON();
}
