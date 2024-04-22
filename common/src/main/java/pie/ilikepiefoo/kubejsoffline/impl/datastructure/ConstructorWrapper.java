package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;

public class ConstructorWrapper implements ConstructorData {
    protected final CollectionGroup collectionGroup;
    protected final Constructor<?> constructor;
    protected List<AnnotationID> annotations;
    protected List<TypeVariableID> typeParameters;
    protected List<TypeID> exceptions;
    protected List<ParameterID> parameters;

    public ConstructorWrapper(CollectionGroup collectionGroup, Constructor<?> constructor) {
        this.collectionGroup = collectionGroup;
        this.constructor = constructor;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        if (getModifiers() != 0) {
            json.addProperty(JSONProperty.MODIFIERS.jsName, getModifiers());
        }
        if (!getAnnotations().isEmpty()) {
            json.add(JSONProperty.ANNOTATIONS.jsName, JSONSerializable.of(getAnnotations()));
        }
        if (!getTypeParameters().isEmpty()) {
            json.add(JSONProperty.TYPE_VARIABLES.jsName, JSONSerializable.of(getTypeParameters()));
        }
        if (!getParameters().isEmpty()) {
            json.add(JSONProperty.PARAMETERS.jsName, JSONSerializable.of(getParameters()));
        }
        if (!getExceptions().isEmpty()) {
            json.add(JSONProperty.EXCEPTIONS.jsName, JSONSerializable.of(getExceptions()));
        }
        return json;
    }

    @Override
    public int getModifiers() {
        return constructor.getModifiers();
    }

    @Override
    public List<AnnotationID> getAnnotations() {
        if (annotations != null) {
            return annotations;
        }
        return this.annotations = collectionGroup.of(constructor.getAnnotations());
    }

    @Override
    public List<TypeVariableID> getTypeParameters() {
        if (typeParameters != null) {
            return typeParameters;
        }
        return this.typeParameters = collectionGroup.of(constructor.getTypeParameters());
    }

    @Override
    public List<TypeID> getExceptions() {
        if (exceptions != null) {
            return exceptions;
        }
        return this.exceptions = collectionGroup.of(constructor.getExceptionTypes());
    }

    @Override
    public List<ParameterID> getParameters() {
        if (parameters != null) {
            return parameters;
        }
        return this.parameters = collectionGroup.of(constructor.getParameters(), SafeOperations.tryGet(constructor::getGenericParameterTypes).orElse(new Type[0]));
    }

    @Override
    public int hashCode() {
        return this.constructor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return this.hashCode() == obj.hashCode();
    }
}
