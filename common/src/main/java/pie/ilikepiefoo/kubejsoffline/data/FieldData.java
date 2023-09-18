package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FieldData implements JSONLike {
	private final int modifiers;
	private final String name;
	private final TypeData type;
	private List<AnnotationData> annotations;
	private Object value;

	public FieldData( int modifiers, String name, TypeData type ) {
		this.modifiers = modifiers;
		this.name = name;
		this.type = type;
	}

	public int getModifiers() {
		return modifiers;
	}

	@Nonnull
	public String getName() {
		return name;
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	public void setValue(@Nullable Object value) {
		this.value = value;
	}

	public FieldData addValue(@Nullable Object value) {
		setValue(value);
		return this;
	}

	@Override
	public JsonElement toJSON() {
		JsonObject object = new JsonObject();
		object.addProperty(JSONProperty.MODIFIERS.jsName, modifiers);
		object.addProperty(JSONProperty.FIELD_NAME.jsName, name);
		// TODO: Fix this
		object.add(JSONProperty.FIELD_TYPE.jsName, getType().toJSON());
		if (value != null) {
			object.addProperty(JSONProperty.FIELD_VALUE.jsName, value.toString());
		}
		return object;
	}

	@Nonnull
	public TypeData getType() {
		return type;
	}

	public void addAnnotations( AnnotationData[] data ) {
		if (this.annotations == null) {
			this.annotations = new ArrayList<>();
		}
		getAnnotations().addAll(List.of(data));
	}

	public List<AnnotationData> getAnnotations() {
		if (annotations == null) {
			return List.of();
		}
		return annotations;
	}

}
