package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.RelationType;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

public class ClassJSONManager {
	private JsonArray typeData;
	private final AtomicInteger typeIDs;
	private ConcurrentHashMap<String, Integer> typeIDMap;
	private static ClassJSONManager INSTANCE;

	public static ClassJSONManager getInstance() {
		if(INSTANCE == null)
			INSTANCE = new ClassJSONManager();
		return INSTANCE;
	}

	public ClassJSONManager() {
		typeData = new JsonArray();
		typeIDs = new AtomicInteger(-1);
		typeIDMap = new ConcurrentHashMap<>();
	}

	public void clear() {
		typeIDMap.clear();
		typeData = new JsonArray();
		typeIDs.set(-1);
		typeIDMap = new ConcurrentHashMap<>();
	}

	public Integer getTypeID(Type type) {
		return getTypeID(SafeOperations.safeUnwrapReturnTypeName(type));
	}

	public Integer getTypeID(String typeName) {
		if(typeName == null)
			return null;
		if(typeIDMap.containsKey(typeName))
			return typeIDMap.get(typeName);
		synchronized (this) {
			int id = typeIDs.incrementAndGet();
			typeIDMap.put(typeName, id);
			JsonObject object = new JsonObject();
			object.addProperty("id", id);
			object.addProperty("name", typeName);
			typeData.add(object);
			return id;
		}
	}

	public JsonObject getTypeData(Type type) {
		return getTypeData(SafeOperations.safeUnwrapReturnTypeName(type));
	}

	public JsonObject getTypeData(String typeName) {
		if(typeName == null)
			return null;
		return typeData.get(getTypeID(typeName)).getAsJsonObject();
	}

	public JsonArray getTypeData() {
		return typeData;
	}

	public JsonArray findAllRelationsOf(Type type, RelationType... relations) {
		return findAllRelationsOf(SafeOperations.safeUnwrapReturnTypeName(type), relations);
	}

	public JsonArray findAllRelationsOf(String typeName, RelationType... relations) {
		JsonArray array = new JsonArray();
		Set<Integer> ids = ConcurrentHashMap.newKeySet();
		Stack<Integer> to_search = new Stack<>();
		to_search.add(getTypeID(typeName));
		while(!to_search.isEmpty()) {
			int id = to_search.pop(); // Get the next id to search.
			if(ids.contains(id)) // Skip if already searched.
				continue;
			ids.add(id); // Mark as searched.
			array.add(id); // Add to the array.
			JsonObject object = typeData.get(id).getAsJsonObject(); // Get the object.
			if(object == null) // Skip if null.
				continue;
			for(RelationType relationType : relations) { // For each relation type.
				var element = object.getAsJsonArray(relationType.getKeyName()); // Get the relation.
				if(element != null){ // If the object has the relation type.
					to_search.addAll(
							StreamSupport.stream(element.spliterator(), false)
									.mapToInt(JsonElement::getAsInt)
									.boxed().toList()); // Add all related ids to the search stack.
				}
			}
		}
		return array; // Return the array of ids.
	}
}
