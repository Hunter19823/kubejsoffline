package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommonData extends TypeData {
	protected final int modifiers;
	protected List<ParameterData> parameters;
	protected List<AnnotationData> annotations;
	protected TypeData type;
	protected Object value;

	public CommonData( String name, int modifiers ) {
		super(name);
		this.modifiers = modifiers;
	}

	@Nonnull
	public CommonData addParameters( @Nonnull ParameterData... data ) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		getParameters().addAll(List.of(data));
		return this;
	}

	@Nonnull
	public List<ParameterData> getParameters() {
		if (parameters == null) {
			return List.of();
		}
		return parameters;
	}

	@Nonnull
	public CommonData addAnnotations( @Nonnull AnnotationData... data ) {
		if (annotations == null) {
			annotations = new ArrayList<>();
		}
		getAnnotations().addAll(List.of(data));
		return this;
	}

	@Nonnull
	public List<AnnotationData> getAnnotations() {
		if (annotations == null) {
			return List.of();
		}
		return annotations;
	}

	public int getModifiers() {
		return modifiers;
	}

	@Nullable
	public Object getValue() {
		return value;
	}

	public void setValue(@Nullable Object value) {
		this.value = value;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public TypeData getType() {
		return type;
	}

	public void setType( @Nullable TypeData returnType ) {
		this.type = returnType;
	}

	@Override
	public JsonElement toJSON() {
		JsonObject object = (JsonObject) super.toJSON();
		object.addProperty(JSONProperty.MODIFIERS.jsName, modifiers);
		if (!getParameters().isEmpty()) {
			object.add(JSONProperty.PARAMETERS.jsName, JSONLike.toJSON(getParameters()));
		}
		if (!getAnnotations().isEmpty()) {
			object.add(JSONProperty.ANNOTATIONS.jsName, JSONLike.toJSON(getAnnotations()));
		}
		if (value != null) {
			object.addProperty(JSONProperty.EXECUTABLE_VALUE.jsName, value.toString());
		}
		if (type != null) {
			// TODO: Fix this
			object.add(JSONProperty.EXECUTABLE_TYPE.jsName, type.toJSON());
		}
		return object;
	}
}
