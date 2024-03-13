package pie.ilikepiefoo.kubejsoffline.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;

public interface JSONSerializable {
    static <S extends JSONSerializable> JsonArray of(List<S> jsonSerializableList) {
        JsonArray jsonArray = new JsonArray();
        for (JSONSerializable jsonSerializable : jsonSerializableList) {
            jsonArray.add(jsonSerializable.toJSON());
        }
        return jsonArray;
    }

    JsonElement toJSON();
}
