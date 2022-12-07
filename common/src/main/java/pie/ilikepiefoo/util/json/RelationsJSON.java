package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.Relation;
import pie.ilikepiefoo.util.RelationType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RelationsJSON {

	@Nonnull
	public static JsonArray of(@Nonnull Set<Relation> relations) {
		ConcurrentHashMap<Integer, Map<RelationType, Set<Integer>>> uniqueRelations = new ConcurrentHashMap<>();
		relations.forEach((connection) -> {
			try {
				JsonObject from = ClassJSONManager.getInstance().getTypeData(connection.from());


				Integer to_id = ClassJSONManager.getInstance().getTypeID(connection.to());

				if(to_id == null)
					return;

				if(from == null)
					return;

				var relation = from.getAsJsonArray(""+connection.relation().getKeyName());

				if (relation == null) {
					relation = new JsonArray();
					from.add(connection.relation().getKeyName(), relation);
				}
				var relationMap = uniqueRelations.computeIfAbsent(from.get(JSONProperty.TYPE_ID.jsName).getAsInt(), (k) -> Collections.synchronizedMap(new EnumMap<>(RelationType.class)));
				var relationSet = relationMap.computeIfAbsent(connection.relation(), (k) -> ConcurrentHashMap.newKeySet());
				if(relationSet.contains(to_id))
					return;
				relation.add(to_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		uniqueRelations.forEach((key,value) -> {
			value.forEach((relationType, relationSet) -> relationSet.clear());
			value.clear();
		});
		uniqueRelations.clear();
		return ClassJSONManager.getInstance().getTypeData();
	}
}