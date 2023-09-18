package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ClassData extends CommonData {
	protected final int id;
	@Nonnull
	protected final String fullyQualifiedName;
	protected ClassData outerClass;
	@Nonnull
	protected Class<?> sourceClass;

	private List<ClassData> superClasses;
	private List<ClassData> implementedInterfaces;
	private List<ConstructorData> constructors;
	private List<FieldData> fields;
	private List<MethodData> methods;
	private List<TypeData> genericParameters;

	public ClassData( @Nonnull String fullyQualifiedName, int modifiers, Class<?> sourceClass, int id ) {
		super(fullyQualifiedName, modifiers);
		this.id = id;
		this.fullyQualifiedName = fullyQualifiedName;
		this.sourceClass = sourceClass;
	}

	@Nonnull
	public ClassData addSuperClasses( @Nonnull ClassData... data ) {
		if (this.superClasses == null) {
			this.superClasses = new ArrayList<>();
		}
		getSuperClasses().addAll(List.of(data));
		return this;
	}

	@Nonnull
	public List<ClassData> getSuperClasses() {
		if (superClasses == null) {
			return List.of();
		}
		return superClasses;
	}

	@Nonnull
	public ClassData addImplementedInterfaces( @Nonnull ClassData... data ) {
		if (this.implementedInterfaces == null) {
			this.implementedInterfaces = new ArrayList<>();
		}
		getImplementedInterfaces().addAll(List.of(data));
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
	public ClassData addFields( @Nonnull FieldData... data ) {
		if (this.fields == null) {
			this.fields = new ArrayList<>();
		}
		getFields().addAll(List.of(data));
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
	public ClassData addMethods( @Nonnull MethodData... data ) {
		if (methods == null) {
			this.methods = new ArrayList<>();
		}
		getMethods().addAll(List.of(data));
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
	public ClassData addConstructors( @Nonnull ConstructorData... data ) {
		if (this.constructors == null) {
			this.constructors = new ArrayList<>();
		}
		getConstructors().addAll(List.of(data));
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
	public ClassData addGenericParameters( @Nonnull TypeData... data ) {
		if (this.genericParameters == null) {
			this.genericParameters = new ArrayList<>();
		}
		getGenericParameters().addAll(List.of(data));
		return this;
	}

	@Nonnull
	public List<TypeData> getGenericParameters() {
		if (genericParameters == null) {
			return List.of();
		}
		return genericParameters;
	}

	@Nullable
	public ClassData getOuterClass() {
		return outerClass;
	}

	@Nonnull
	public ClassData setOuterClass( @Nonnull ClassData outerClass ) {
		this.outerClass = outerClass;
		return this;
	}

	@Nonnull
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public void setSourceClass( Class<?> sourceClass ) {
		this.sourceClass = sourceClass;
	}

	@Override
	public JsonElement toJSON() {
		var output = super.toJSON().getAsJsonObject();
		output.addProperty(JSONProperty.TYPE_ID.jsName, getId());
		output.addProperty(JSONProperty.TYPE_IDENTIFIER.jsName, getFullyQualifiedName());
		if (!getSuperClasses().isEmpty()) {
			output.add(JSONProperty.SUPER_CLASS.jsName, JSONLike.toJSON(getSuperClasses().stream().map(ClassData::getId).toList()));
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
			// TODO: Fix this
			output.add(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName, JSONLike.toJSON(getGenericParameters()));
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
