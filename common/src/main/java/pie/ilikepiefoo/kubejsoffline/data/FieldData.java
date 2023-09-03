package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FieldData implements JSONLike {
	private final int modifiers;
	private final String name;
	private final ClassData type;
	private Object value;

	public FieldData(int modifiers, String name, ClassData type) {
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
		object.addProperty(JSONProperty.FIELD_TYPE.jsName, getType().getId());
		if (value != null) {
			object.addProperty(JSONProperty.FIELD_VALUE.jsName, value.toString());
		}
		return object;
	}

	@Nonnull
	public ClassData getType() {
		return type;
	}
}
