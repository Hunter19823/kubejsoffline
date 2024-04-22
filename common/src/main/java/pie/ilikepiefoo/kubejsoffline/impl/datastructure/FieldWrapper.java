package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.FieldData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.Field;
import java.util.List;

public class FieldWrapper implements FieldData {
    protected final CollectionGroup collectionGroup;
    protected final Field field;
    protected TypeOrTypeVariableID type;
    protected NameID name;
    protected List<AnnotationID> annotations;


    public FieldWrapper(CollectionGroup collectionGroup, Field field) {
        this.collectionGroup = collectionGroup;
        this.field = field;
    }

    @Override
    public JsonElement toJSON() {
        JsonObject json = new JsonObject();
        json.add(JSONProperty.FIELD_NAME.jsName, getName().toJSON());
        json.add(JSONProperty.FIELD_TYPE.jsName, getType().toJSON());

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
        return this.name = collectionGroup.names().addName(SafeOperations.safeRemap(field));
    }

    @Override
    public TypeOrTypeVariableID getType() {
        if (type != null) {
            return type;
        }
        return this.type = collectionGroup.of(SafeOperations.tryGetFirst(field::getGenericType, field::getType).orElseThrow(NullPointerException::new));
    }

    @Override
    public int getModifiers() {
        return field.getModifiers();
    }

    @Override
    public List<AnnotationID> getAnnotations() {
        if (annotations != null) {
            return annotations;
        }
        return this.annotations = collectionGroup.of(field.getAnnotations());
    }

    @Override
    public int hashCode() {
        return this.field.hashCode();
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
