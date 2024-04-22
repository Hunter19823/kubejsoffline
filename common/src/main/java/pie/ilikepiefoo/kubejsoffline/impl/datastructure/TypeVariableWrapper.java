package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.ReflectionUtils;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Objects;

public class TypeVariableWrapper implements TypeVariableData {
    protected final CollectionGroup collectionGroup;
    protected final TypeVariable<?> typeVariable;
    protected TypeVariableID index;
    protected NameID name;
    protected List<TypeOrTypeVariableID> bounds;

    public TypeVariableWrapper(CollectionGroup collectionGroup, TypeVariable<?> typeVariable) {
        this.collectionGroup = collectionGroup;
        this.typeVariable = typeVariable;
    }

    @Override
    public TypeVariableData setIndex(TypeOrTypeVariableID index) {
        this.index = index.asTypeVariable();
        return this;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        json.add(JSONProperty.TYPE_VARIABLE_NAME.jsName, getName().toJSON());
        if (getBounds().isEmpty()) {
            return json;
        }
        json.add(JSONProperty.TYPE_VARIABLE_BOUNDS.jsName, JSONSerializable.of(getBounds()));
        return json;
    }

    @Override
    public synchronized NameID getName() {
        if (name != null) {
            return name;
        }
        return name = collectionGroup.names().addName(typeVariable.getName());
    }

    @Override
    public synchronized List<TypeOrTypeVariableID> getBounds() {
        if (bounds != null) {
            return bounds;
        }
        return this.bounds = collectionGroup.of(typeVariable.getBounds(), (Type type) -> type == Object.class);
    }

    @Override
    public TypeVariableID getIndex() {
        return index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getName(),
                ReflectionUtils.hashCode(typeVariable.getBounds())
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
        if (obj instanceof TypeVariableWrapper other) {
            return (this.typeVariable.equals(other.typeVariable)) || (this.hashCode() == other.hashCode());
        }

        return false;
    }
}
