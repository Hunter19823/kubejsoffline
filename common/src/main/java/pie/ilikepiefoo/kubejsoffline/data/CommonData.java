package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommonData implements JSONLike {
	private final int modifiers;
	private List<ParameterData> parameters;
	private List<AnnotationData> annotations;
	private String name;
	private ClassData type;
	private Object value;

	public CommonData(int modifiers) {
		this.modifiers = modifiers;
	}

	@Nonnull
	public CommonData addParameter(@Nonnull ParameterData parameterData) {
		if (parameters == null) {
			parameters = new ArrayList<>();
		}
		getParameters().add(parameterData);
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
	public CommonData addAnnotation(@Nonnull AnnotationData annotationData) {
		if (annotations == null) {
			annotations = new ArrayList<>();
		}
		getAnnotations().add(annotationData);
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

	public String getName() {
		return name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	public ClassData getType() {
		return type;
	}

	public void setType(@Nullable ClassData returnType) {
		this.type = returnType;
	}

	@Override
	public JsonElement toJSON() {
		JsonObject object = new JsonObject();
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
		if (name != null) {
			object.addProperty(JSONProperty.EXECUTABLE_NAME.jsName, name);
		}
		if (type != null) {
			object.addProperty(JSONProperty.EXECUTABLE_RETURN_TYPE.jsName, type.getId());
		}
		return object;
	}
}
