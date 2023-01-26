package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.RelationType;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	@Nullable
	public Integer getTypeID(@Nullable Type type) {
		return getTypeID(SafeOperations.safeUniqueTypeName(type));
	}

	@Nullable
	public Integer getTypeID(@Nullable String typeName) {
		if(typeName == null || typeName.isBlank())
			return null;
		if(typeIDMap.containsKey(typeName))
			return typeIDMap.get(typeName);
		synchronized (this) {
			int id = typeIDs.incrementAndGet();
			typeIDMap.put(typeName, id);
			JsonObject object = new JsonObject();
			object.addProperty(JSONProperty.TYPE_ID.jsName, id);
			typeData.add(object);
			object.addProperty(JSONProperty.TYPE_IDENTIFIER.jsName, typeName);
			return id;
		}
	}

	@Nullable
	public JsonObject getTypeData(@Nullable Type type) {
		return getTypeData(SafeOperations.safeUniqueTypeName(type));
	}

	@Nullable
	public JsonObject getTypeData(@Nullable String typeName) {
		if(typeName == null)
			return null;
		Integer id = getTypeID(typeName);
		if(id == null)
			return null;
		return typeData.get(id).getAsJsonObject();
	}

	@Nonnull
	public JsonArray getTypeData() {
		return typeData;
	}

	@Nonnull
	public JsonArray findAllRelationsOf(@Nonnull Type type, @Nonnull RelationType... relations) {
		return findAllRelationsOf(SafeOperations.safeUnwrapReturnTypeName(type), relations);
	}

	@Nonnull
	public JsonArray findAllRelationsOf(@Nonnull String typeName, @Nonnull RelationType... relations) {
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
