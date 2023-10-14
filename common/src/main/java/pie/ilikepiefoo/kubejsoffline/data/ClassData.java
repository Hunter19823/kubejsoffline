package pie.ilikepiefoo.kubejsoffline.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import pie.ilikepiefoo.kubejsoffline.data.populate.DataMapper;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClassData extends TypeData {
    public final AtomicBoolean populated = new AtomicBoolean(false);
    @Nonnull
    protected final String fullyQualifiedName;
    protected final int modifiers;
    protected ClassData outerClass;
    @Nonnull
    protected Class<?> sourceClass;

    protected Set<AnnotationData> annotations;
    protected Set<TypeData> superClasses;
    protected Set<TypeData> implementedInterfaces;
    protected Set<ConstructorData> constructors;
    protected Set<FieldData> fields;
    protected Set<MethodData> methods;
    protected List<TypeData> genericParameters;

    public ClassData(@Nonnull String fullyQualifiedName, int modifiers, @NotNull Class<?> sourceClass) {
        super(fullyQualifiedName);
        this.modifiers = modifiers;
        this.fullyQualifiedName = fullyQualifiedName;
        this.sourceClass = sourceClass;
    }

    @Nonnull
    public ClassData addSuperClasses(@Nonnull TypeData... data) {
        if (this.superClasses == null) {
            this.superClasses = new HashSet<>();
        }
        getSuperClasses().addAll(List.of(data));
        return this;
    }

    @Nonnull
    public Collection<TypeData> getSuperClasses() {
        if (superClasses == null) {
            return List.of();
        }
        return superClasses;
    }

    public void setSuperClasses(@Nonnull TypeData... data) {
        this.superClasses = Set.of(data);
    }

    @Nonnull
    public ClassData addImplementedInterfaces(@Nonnull TypeData... data) {
        if (this.implementedInterfaces == null) {
            this.implementedInterfaces = new HashSet<>();
        }
        getImplementedInterfaces().addAll(List.of(data));
        return this;
    }

    @Nonnull
    public Collection<TypeData> getImplementedInterfaces() {
        return Objects.requireNonNullElseGet(implementedInterfaces, List::of);
    }

    public void setImplementedInterfaces(@Nonnull TypeData... data) {
        this.implementedInterfaces = Set.of(data);
    }

    @Nonnull
    public synchronized ClassData addFields(@Nonnull FieldData... data) {
        if (this.fields == null) {
            this.fields = new HashSet<>();
        }
        getFields().addAll(List.of(data));
        return this;
    }

    @Nonnull
    public ClassData addMethods(@Nonnull MethodData... data) {
        if (methods == null) {
            this.methods = new HashSet<>();
        }
        getMethods().addAll(List.of(data));
        return this;
    }

    public void setFields(@Nonnull FieldData... data) {
        this.fields = Set.of(data);
    }

    @Override
    public JsonElement toJSON() {
        var output = super.toJSON().getAsJsonObject();
        output.addProperty(JSONProperty.MODIFIERS.jsName, getModifiers());
        output.addProperty(JSONProperty.TYPE_ID.jsName, getId());

        if (!getGenericParameters().isEmpty()) {
            JsonArray array = new JsonArray();
            for (TypeData typeData : getGenericParameters()) {
                array.add(typeData.toReference());
            }
            output.add(JSONProperty.CLASS_PARAMETERIZED_ARGUMENTS.jsName, array);
        }
        if (getOuterClass() != null) {
            output.addProperty(JSONProperty.OUTER_CLASS.jsName, getOuterClass().getId());
        }
        if (!getSuperClasses().isEmpty()) {
            output.add(JSONProperty.SUPER_CLASS.jsName, JSONLike.toJSON(getSuperClasses().stream().map(typeData -> typeData.toReference()).toList()));
        }
        if (!getImplementedInterfaces().isEmpty()) {
            output.add(JSONProperty.INTERFACES.jsName, JSONLike.toJSON(getImplementedInterfaces().stream().map(typeData -> typeData.toReference()).toList()));
        }
        if (!getFields().isEmpty()) {
            output.add(JSONProperty.FIELDS.jsName, JSONLike.toJSON(getFields()));
        }
        if (!getMethods().isEmpty()) {
            output.add(JSONProperty.METHODS.jsName, JSONLike.toJSON(getMethods()));
        }

        return output;
    }

    @Nonnull
    public Collection<MethodData> getMethods() {
        if (methods == null) {
            return List.of();
        }
        return methods;
    }

    public void setMethods(@Nonnull MethodData... data) {
        this.methods = Set.of(data);
    }

    @Nonnull
    public synchronized ClassData addConstructors(@Nonnull ConstructorData... data) {
        if (this.constructors == null) {
            this.constructors = new HashSet<>();
        }
        getConstructors().addAll(List.of(data));
        return this;
    }

    @Nonnull
    public Collection<ConstructorData> getConstructors() {
        if (methods == null) {
            return List.of();
        }
        return constructors;
    }

    public void setConstructors(@Nonnull ConstructorData... data) {
        this.constructors = Set.of(data);
    }

    @Nonnull
    public synchronized ClassData addGenericParameters(@Nonnull TypeData... data) {
        if (this.genericParameters == null) {
            this.genericParameters = new ArrayList<>();
        }
        getGenericParameters().addAll(List.of(data));
        return this;
    }

    @Nonnull
    public Collection<TypeData> getGenericParameters() {
        if (genericParameters == null) {
            return List.of();
        }
        return genericParameters;
    }

    public void setGenericParameters(@Nonnull TypeData... data) {
        this.genericParameters = List.of(data);
    }

    @Nonnull
    public synchronized ClassData addAnnotations(@Nonnull AnnotationData... data) {
        if (annotations == null) {
            annotations = new HashSet<>();
        }
        getAnnotations().addAll(List.of(data));
        return this;
    }

    @Nonnull
    public Collection<AnnotationData> getAnnotations() {
        if (annotations == null) {
            return List.of();
        }
        return annotations;
    }

    public void setAnnotations(@Nonnull AnnotationData... data) {
        this.annotations = Set.of(data);
    }

    public int getId() {
        return DataMapper.getInstance().getIdentifier(this);
    }

    public int getModifiers() {
        return modifiers;
    }

    @Nonnull
    public Collection<FieldData> getFields() {
        return Objects.requireNonNullElseGet(fields, List::of);
    }

    @Override
    public JsonObject toReference() {
        JsonObject object = new JsonObject();
        object.addProperty(JSONProperty.CLASS_REFERENCE.jsName, this.getId());
        return object;
    }

    @Nullable
    public ClassData getOuterClass() {
        return outerClass;
    }

    @Nonnull
    public ClassData setOuterClass(@Nonnull ClassData outerClass) {
        this.outerClass = outerClass;
        return this;
    }

    @Nonnull
    public Class<?> getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(Class<?> sourceClass) {
        this.sourceClass = sourceClass;
    }

    @Override
    public int hashCode() {
        return getFullyQualifiedName().hashCode();
    }

    @Nonnull
    public String getFullyQualifiedName() {
        return fullyQualifiedName;
    }
}
