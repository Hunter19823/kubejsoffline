package pie.ilikepiefoo.kubejsoffline.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public interface JSONSerializable {
    Logger LOG = LogManager.getLogger();

    static <S extends JSONSerializable> JsonArray of(Collection<S> jsonSerializableList) {
        JsonArray jsonArray = new JsonArray();
        for (JSONSerializable jsonSerializable : jsonSerializableList) {
            try {
                jsonArray.add(jsonSerializable.toJSON());
            } catch (final Throwable e) {
                LOG.warn("Failed to convert JSONSerializable to JSONElement", e);
            }
        }
        return jsonArray;
    }

    JsonElement toJSON();
}
