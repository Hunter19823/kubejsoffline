package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.MethodData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

public class MethodWrapper implements MethodData {
    protected final CollectionGroup collectionGroup;
    protected final Method method;
    protected List<AnnotationID> annotations;
    protected List<TypeVariableID> typeParameters;
    protected List<TypeID> exceptions;
    protected List<ParameterID> parameters;
    protected TypeOrTypeVariableID type;
    protected NameID name;


    public MethodWrapper(CollectionGroup collectionGroup, Method method) {
        this.collectionGroup = collectionGroup;
        this.method = method;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        json.add(JSONProperty.METHOD_NAME.jsName, getName().toJSON());

        if (getModifiers() != 0) {
            json.addProperty(JSONProperty.MODIFIERS.jsName, method.getModifiers());
        }
        json.add(JSONProperty.METHOD_RETURN_TYPE.jsName, getType().toJSON());
        if (!getAnnotations().isEmpty()) {
            json.add(JSONProperty.ANNOTATIONS.jsName, JSONSerializable.of(getAnnotations()));
        }
        if (!getParameters().isEmpty()) {
            json.add(JSONProperty.PARAMETERS.jsName, JSONSerializable.of(getParameters()));
        }
        if (!getTypeParameters().isEmpty()) {
            json.add(JSONProperty.TYPE_VARIABLES.jsName, JSONSerializable.of(getTypeParameters()));
        }
        if (!getExceptions().isEmpty()) {
            json.add(JSONProperty.EXCEPTIONS.jsName, JSONSerializable.of(getExceptions()));
        }
        return json;
    }

    @Override
    public NameID getName() {
        if (name != null) {
            return name;
        }
        return this.name = collectionGroup.names().addName(SafeOperations.safeRemap(method));
    }

    @Override
    public int getModifiers() {
        return method.getModifiers();
    }

    @Override
    public TypeOrTypeVariableID getType() {
        if (type != null) {
            return type;
        }
        return this.type = collectionGroup.of(method.getGenericReturnType());
    }

    @Override
    public List<AnnotationID> getAnnotations() {
        if (annotations != null) {
            return annotations;
        }
        return this.annotations = collectionGroup.of(method.getAnnotations());
    }

    @Override
    public List<TypeVariableID> getTypeParameters() {
        if (typeParameters != null) {
            return typeParameters;
        }
        return this.typeParameters = collectionGroup.of(method.getTypeParameters());
    }

    @Override
    public List<TypeID> getExceptions() {
        if (exceptions != null) {
            return exceptions;
        }
        return this.exceptions = collectionGroup.of(method.getExceptionTypes());
    }

    @Override
    public List<ParameterID> getParameters() {
        if (parameters != null) {
            return parameters;
        }
        return this.parameters = collectionGroup.of(method.getParameters(), SafeOperations.tryGet(method::getGenericParameterTypes).orElse(new Type[0]));
    }

    @Override
    public int hashCode() {
        return this.method.hashCode();
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
