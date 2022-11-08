package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClassJSONManager {
	private final JsonArray typeData;
	private final AtomicInteger typeIDs;
	private final ConcurrentHashMap<String, Integer> typeIDMap;
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

	public Integer getTypeID(Type type) {
		return getTypeID(SafeOperations.safeUnwrapReturnType(type));
	}

	public Integer getTypeID(String typeName) {
		if(typeName == null)
			return null;
		if(typeIDMap.containsKey(typeName))
			return typeIDMap.get(typeName);
		int id = typeIDs.getAcquire() + 1;
		typeIDMap.put(typeName, id);
		JsonObject object = new JsonObject();
		object.addProperty("id", id);
		object.addProperty("name", typeName);
		typeData.add(object);
		typeIDs.setRelease(id);
		return id;
	}

	public JsonObject getTypeData(Type type) {
		return getTypeData(SafeOperations.safeUnwrapReturnType(type));
	}

	public JsonObject getTypeData(String typeName) {
		if(typeName == null)
			return null;
		return typeData.get(getTypeID(typeName)).getAsJsonObject();
	}

	public JsonArray getTypeData() {
		return typeData;
	}
}
