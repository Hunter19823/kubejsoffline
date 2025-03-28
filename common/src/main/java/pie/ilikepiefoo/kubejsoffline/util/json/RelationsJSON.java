package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.Relation;
import pie.ilikepiefoo.kubejsoffline.util.RelationType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RelationsJSON {

    @Nonnull
    public static JsonArray of(@Nonnull final Set<Relation> relations) {
        final ConcurrentHashMap<Integer, Map<RelationType, Set<Integer>>> uniqueRelations = new ConcurrentHashMap<>();
        relations.forEach((connection) -> {
            try {
                final JsonObject from = ClassJSONManager.getInstance().getTypeData(connection.from());


                final Integer to_id = ClassJSONManager.getInstance().getTypeID(connection.to());

                if (null == to_id) {
                    return;
                }

                if (null == from) {
                    return;
                }

                var relation = from.getAsJsonArray(connection.relation().getKeyName());

                if (null == relation) {
                    relation = new JsonArray();
                    from.add(connection.relation().getKeyName(), relation);
                }
                final var relationMap = uniqueRelations.computeIfAbsent(from.get(JSONProperty.TYPE_ID.jsName).getAsInt(), (k) -> Collections.synchronizedMap(new EnumMap<>(RelationType.class)));
                final var relationSet = relationMap.computeIfAbsent(connection.relation(), (k) -> ConcurrentHashMap.newKeySet());
                if (relationSet.contains(to_id)) {
                    return;
                }
                relation.add(to_id);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
        uniqueRelations.forEach((key, value) -> {
            value.forEach((relationType, relationSet) -> relationSet.clear());
            value.clear();
        });
        uniqueRelations.clear();
        return ClassJSONManager.getInstance().getTypeData();
    }
}
