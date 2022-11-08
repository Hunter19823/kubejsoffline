package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.Connection;

import java.util.Set;

public class RelationsJSON {
	public static JsonArray of(Set<Connection> relations) {
		relations.forEach((connection) -> {
			try {
				String relation_name = connection.relation().name();

				JsonObject from = ClassJSONManager.getInstance().getTypeData(connection.from());

				Integer to_id = ClassJSONManager.getInstance().getTypeID(connection.to());
				JsonObject relation = from.getAsJsonObject(relation_name);

				if (relation == null) {
					relation = new JsonObject();
					from.add(relation_name, relation);
				}
				if(relation.has(""+to_id))
					return;
				relation.addProperty(""+to_id, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return ClassJSONManager.getInstance().getTypeData();
	}
}
