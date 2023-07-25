package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.DocumentationConfig;
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
	private static final Logger LOG = LogManager.getLogger();

	private JsonArray typeData;
	private final AtomicInteger typeIDs;
	private ConcurrentHashMap<String, Integer> typeIDMap;
	private static ClassJSONManager INSTANCE;

	public ClassJSONManager() {
		this.typeData = new JsonArray();
		this.typeIDs = new AtomicInteger(-1);
		this.typeIDMap = new ConcurrentHashMap<>();
	}

	public static ClassJSONManager getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ClassJSONManager();
		}
		return INSTANCE;
	}

	public synchronized void clear() {
		this.typeIDMap.clear();
		this.typeData = new JsonArray();
		this.typeIDs.set(-1);
		this.typeIDMap = new ConcurrentHashMap<>();
	}

	@Nullable
	public Integer getTypeID(@Nullable final Type type) {
		return this.getTypeID(SafeOperations.safeUniqueTypeName(type));
	}

	@Nullable
	public synchronized Integer getTypeID(@Nullable final String typeName) {
		if (null == typeName || typeName.isBlank()) {
			return null;
		}
		final var obj = this.getTypeData(typeName);
		if (null == obj) {
			return null;
		}
		return obj.get(JSONProperty.TYPE_ID.jsName).getAsInt();
	}

	@Nullable
	public synchronized JsonObject getTypeData(@Nullable final String typeName) {
		if (null == typeName || typeName.isBlank()) {
			return null;
		}
		this.typeIDMap.computeIfAbsent(typeName, (key) -> {
			final int id = this.typeIDs.incrementAndGet();
			final JsonObject object = new JsonObject();
			object.addProperty(JSONProperty.TYPE_ID.jsName, id);
			this.typeData.add(object);
			object.addProperty(JSONProperty.TYPE_IDENTIFIER.jsName, CompressionJSON.getInstance().compress(typeName));
			return id;
		});
		final int id = this.typeIDMap.get(typeName);
		return this.typeData.get(id).getAsJsonObject();
	}

	@Nullable
	public JsonObject getTypeData(@Nullable final Type type) {
		return this.getTypeData(SafeOperations.safeUniqueTypeName(type));
	}

	@Nonnull
	public synchronized JsonArray getTypeData() {
		return this.typeData;
	}

	@Nonnull
	public JsonArray findAllRelationsOf(@Nonnull final Type type, @Nonnull final RelationType... relations) {
		return this.findAllRelationsOf(SafeOperations.safeUnwrapReturnTypeName(type), relations);
	}

	@Nonnull
	public JsonArray findAllRelationsOf(@Nonnull final String typeName, @Nonnull final RelationType... relations) {
		final JsonArray array = new JsonArray();
		final Set<Integer> ids = ConcurrentHashMap.newKeySet();
		final Stack<Integer> to_search = new Stack<>();
		to_search.add(this.getTypeID(typeName));
		while (!to_search.isEmpty()) {
			final int id = to_search.pop(); // Get the next id to search.
			if (ids.contains(id)) // Skip if already searched.
			{
				continue;
			}
			ids.add(id); // Mark as searched.
			array.add(id); // Add to the array.
			final JsonObject object = this.typeData.get(id).getAsJsonObject(); // Get the object.
			if (null == object) // Skip if null.
			{
				continue;
			}
			for (final RelationType relationType : relations) { // For each relation type.
				final var element = object.getAsJsonArray(relationType.getKeyName()); // Get the relation.
				if (null != element) { // If the object has the relation type.
					to_search.addAll(
							StreamSupport.stream(element.spliterator(), false)
									.mapToInt(JsonElement::getAsInt)
									.boxed().toList()); // Add all related ids to the search stack.
				}
			}
		}
		return array; // Return the array of ids.
	}

	public void filterRelationshipData() {
		// Stream type data entries.
		// Take the spliterator and stream it.
		// Map each element to a JsonObject.
		// For each JsonObject, remove all relation types that are not enabled.
		StreamSupport.stream(this.typeData.spliterator(), true)
				.map(JsonElement::getAsJsonObject)
				.forEach((object -> {
					if (!DocumentationConfig.getInstance().saveAnyRelationTypeData) {
						for (final RelationType relationType : RelationType.values()) {
							object.remove(relationType.getKeyName());
						}
					} else {
						for (final RelationType relationType : RelationType.values()) {
							if (!DocumentationConfig.getInstance().enabledRelationTypes.contains(relationType)) {
								object.remove(relationType.getKeyName());
							}
						}
					}
				}));
	}
}
