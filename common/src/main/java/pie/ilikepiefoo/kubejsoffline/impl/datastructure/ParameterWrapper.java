package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class ParameterWrapper implements ParameterData {
    protected CollectionGroup collectionGroup;
    protected Parameter parameter;
    protected Type genericType;
    protected ParameterID parameterID;
    protected List<AnnotationID> annotations;
    protected TypeOrTypeVariableID type;
    protected NameID name;

    public ParameterWrapper(CollectionGroup collectionGroup, Parameter parameter, Type genericType) {
        this.collectionGroup = collectionGroup;
        this.parameter = parameter;
        this.genericType = genericType;
    }

    @Override
    public ParameterID getIndex() {
        return parameterID;
    }

    @Override
    public ParameterData setIndex(ParameterID index) {
        this.parameterID = index;
        return this;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        json.add(JSONProperty.PARAMETER_NAME.jsName, getName().toJSON());
        json.add(JSONProperty.PARAMETER_TYPE.jsName, getType().toJSON());

        if (getModifiers() != 0) {
            json.addProperty(JSONProperty.MODIFIERS.jsName, getModifiers());
        }
        if (!getAnnotations().isEmpty()) {
            json.add(JSONProperty.ANNOTATIONS.jsName, JSONSerializable.of(getAnnotations()));
        }

        return json;
    }

    @Override
    public NameID getName() {
        if (name != null) {
            return name;
        }
        return name = collectionGroup.nameOf(parameter.getName());
    }

    @Override
    public TypeOrTypeVariableID getType() {
        if (type != null) {
            return type;
        }
        return type = collectionGroup.of(genericType);
    }

    @Override
    public int getModifiers() {
        return parameter.getModifiers();
    }

    @Override
    public List<AnnotationID> getAnnotations() {
        if (annotations != null) {
            return annotations;
        }
        return annotations = collectionGroup.of(parameter.getAnnotations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getName(),
                getModifiers(),
                getAnnotations(),
                getType()
        );
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
