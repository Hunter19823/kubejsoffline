package pie.ilikepiefoo.kubejsoffline.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CollectionGroupTest {
    public static final Logger LOG = LogManager.getLogger();
    public static final Gson GSON = new GsonBuilder().create();
    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    @Test
    void of() {
        var objectID = TypeManager.INSTANCE.getID(Object.class);

        var json = CollectionGroup.INSTANCE.toJSON();

        assertTrue(json.isJsonObject());
        var jsonObject = json.getAsJsonObject();

        String[] keys = new String[]{
                "types",
                "parameters",
                "packages",
                "names",
                "annotations"
        };
        for (String key : keys) {
            assertTrue(jsonObject.has(key));
            assertTrue(jsonObject.get(key).isJsonArray());
        }

        Map<String, Map<String, List<Map.Entry<Integer, JsonElement>>>> DUPLICATES = new HashMap<>();
        Map<String, Map<String, Integer>> FIRST_SEEN = new HashMap<>();

        for (String key : keys) {
            DUPLICATES.put(key, new HashMap<>());
            FIRST_SEEN.put(key, new HashMap<>());
            var array = jsonObject.getAsJsonArray(key);
            for (int i = 0; i < array.size(); i++) {
                JsonElement id = null;
                JsonElement value = array.get(i);
                if (value.isJsonObject()) {
                    id = value.getAsJsonObject().remove("id");
                }
                var stringify = GSON.toJson(value);
                if (id != null) {
                    value.getAsJsonObject().add("id", id);
                }
                if (!FIRST_SEEN.get(key).containsKey(stringify)) {
                    FIRST_SEEN.get(key).put(stringify, i);
                    continue;
                }
                if (!DUPLICATES.get(key).containsKey(stringify)) {
                    DUPLICATES.get(key).put(stringify, new ArrayList<>());
                    DUPLICATES.get(key).get(stringify).add(Map.entry(FIRST_SEEN.get(key).get(stringify), value));
                }
                DUPLICATES.get(key).get(stringify).add(Map.entry(i, value));
            }
        }


        // Write JSON to file
        try (FileWriter file = new FileWriter("collectiongroup.ignore.json")) {
            GSON.toJson(json, GSON.newJsonWriter(file));
        } catch (Exception e) {
            LOG.error(e);
        }
        try (FileWriter file = new FileWriter("duplicates.ignore.json")) {
            PRETTY_GSON.toJson(PRETTY_GSON.toJsonTree(DUPLICATES), PRETTY_GSON.newJsonWriter(file));
        } catch (Exception e) {
            LOG.error(e);
        }

        if (DUPLICATES.values().stream().anyMatch(map -> map.values().stream().anyMatch(list -> list.size() > 1))) {
            fail("Duplicates found in CollectionGroup JSON");
        }
    }

    @Test
    void typeVariablesNoCircularReferences() {
        var objectID = TypeManager.INSTANCE.getID(Object.class);

        var json = CollectionGroup.INSTANCE.toJSON();

        TypeManager.INSTANCE.collectionGroup.types().getAllTypes().forEach(((typeOrTypeVariableID, typeData) -> {
            if (!typeData.isParameterizedType()) {
                return;
            }
            var parameterizedType = typeData.asParameterizedType();
            assertNotEquals(typeOrTypeVariableID, parameterizedType.getRawType());
            assertNotEquals(typeOrTypeVariableID, parameterizedType.getOwnerType());
        }));

    }

}