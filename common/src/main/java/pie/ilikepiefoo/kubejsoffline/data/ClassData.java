package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ClassData extends CommonData {
	private final int id;
	private final String fullyQualifiedName;

	private List<ClassData> inheritedClasses;
	private List<ClassData> implementedInterfaces;
	private List<ConstructorData> constructors;
	private List<FieldData> fields;
	private List<MethodData> methods;
	private List<ClassData> genericParameters;

	public ClassData(int modifiers, int id, @Nonnull String fullyQualifiedName) {
		super(modifiers);
		this.id = id;
		this.fullyQualifiedName = fullyQualifiedName;
	}

	@Nonnull
	public ClassData addInheritedClass(@Nonnull ClassData inheritedClass) {
		if (inheritedClasses == null) {
			inheritedClasses = new ArrayList<>();
		}
		getInheritedClasses().add(inheritedClass);
		return this;
	}

	@Nonnull
	public List<ClassData> getInheritedClasses() {
		if (inheritedClasses == null) {
			return List.of();
		}
		return inheritedClasses;
	}

	@Nonnull
	public ClassData addImplementedInterface(@Nonnull ClassData implementedInterface) {
		if (implementedInterfaces == null) {
			implementedInterfaces = new ArrayList<>();
		}
		getImplementedInterfaces().add(implementedInterface);
		return this;
	}

	@Nonnull
	public List<ClassData> getImplementedInterfaces() {
		if (implementedInterfaces == null) {
			return List.of();
		}
		return implementedInterfaces;
	}

	@Nonnull
	public ClassData addField(@Nonnull FieldData field) {
		if (fields == null) {
			fields = new ArrayList<>();
		}
		getFields().add(field);
		return this;
	}

	@Nonnull
	public List<FieldData> getFields() {
		if (fields == null) {
			return List.of();
		}
		return fields;
	}

	@Nonnull
	public ClassData addMethod(@Nonnull MethodData method) {
		if (methods == null) {
			methods = new ArrayList<>();
		}
		getMethods().add(method);
		return this;
	}

	@Nonnull
	public List<MethodData> getMethods() {
		if (methods == null) {
			return List.of();
		}
		return methods;
	}

	@Nonnull
	public ClassData addConstructor(@Nonnull ConstructorData constructor) {
		if (constructors == null) {
			constructors = new ArrayList<>();
		}
		getConstructors().add(constructor);
		return this;
	}

	@Nonnull
	public List<ConstructorData> getConstructors() {
		if (methods == null) {
			return List.of();
		}
		return constructors;
	}

	@Nonnull
	public ClassData addGenericParameter(@Nonnull ClassData genericParameter) {
		if (genericParameters == null) {
			genericParameters = new ArrayList<>();
		}
		getGenericParameters().add(genericParameter);
		return this;
	}

	@Nonnull
	public List<ClassData> getGenericParameters() {
		if (genericParameters == null) {
			return List.of();
		}
		return genericParameters;
	}

	@Override
	public JsonElement toJSON() {
		var output = super.toJSON().getAsJsonObject();
		output.addProperty(JSONProperty.TYPE_ID.jsName, getId());
		output.addProperty(JSONProperty.TYPE_IDENTIFIER.jsName, getFullyQualifiedName());
		if (!getInheritedClasses().isEmpty()) {
			output.add(JSONProperty.SUPER_CLASS.jsName, JSONLike.toJSON(getInheritedClasses().stream().map(ClassData::getId).toList()));
		}
		if (!getImplementedInterfaces().isEmpty()) {
			output.add(JSONProperty.INTERFACES.jsName, JSONLike.toJSON(getImplementedInterfaces().stream().map(ClassData::getId).toList()));
		}
		if (!getFields().isEmpty()) {
			output.add(JSONProperty.FIELDS.jsName, JSONLike.toJSON(getFields()));
		}
		if (!getMethods().isEmpty()) {
			output.add(JSONProperty.METHODS.jsName, JSONLike.toJSON(getMethods()));
		}
		if (!getGenericParameters().isEmpty()) {
			output.add(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName, JSONLike.toJSON(getGenericParameters().stream().map(ClassData::getId).toList()));
		}

		return output;
	}

	public int getId() {
		return id;
	}

	@Nonnull
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@Override
	public int hashCode() {
		return getFullyQualifiedName().hashCode();
	}
}
