package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

public class ParameterizedTypeWrapper implements ParameterizedTypeData {
    protected final CollectionGroup collectionGroup;
    protected final ParameterizedType parameterizedType;
    protected TypeID index;
    protected List<TypeOrTypeVariableID> actualTypeArguments;
    protected TypeID ownerType;
    protected TypeID rawType;

    public ParameterizedTypeWrapper(CollectionGroup collectionGroup, ParameterizedType parameterizedType) {
        this.collectionGroup = collectionGroup;
        this.parameterizedType = parameterizedType;
    }

    @Override
    public TypeID getIndex() {
        return index;
    }

    @Override
    public ParameterizedTypeData setIndex(TypeOrTypeVariableID index) {
        this.index = index.asType();
        return this;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        json.add(JSONProperty.RAW_PARAMETERIZED_TYPE.jsName, getRawType().toJSON());
        if (getOwnerType() != null) {
            json.add(JSONProperty.OWNER_TYPE.jsName, getOwnerType().toJSON());
        }
        if (getActualTypeArguments().isEmpty()) {
            return json;
        }
        json.add(JSONProperty.TYPE_VARIABLES.jsName, JSONSerializable.of(getActualTypeArguments()));
        return json;
    }

    @Override
    public synchronized TypeID getRawType() {
        if (rawType != null) {
            return rawType;
        }
        return this.rawType = collectionGroup.of(parameterizedType.getRawType()).asType();
    }

    @Override
    public synchronized List<TypeOrTypeVariableID> getActualTypeArguments() {
        if (actualTypeArguments != null) {
            return actualTypeArguments;
        }
        return this.actualTypeArguments = collectionGroup.of(parameterizedType.getActualTypeArguments());
    }

    @Override
    public synchronized TypeID getOwnerType() {
        if (ownerType != null) {
            return ownerType;
        }

        if (parameterizedType.getOwnerType() == null) {
            return null;
        }

        return this.ownerType = collectionGroup.of(parameterizedType.getOwnerType()).asType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getRawType(),
                getOwnerType(),
                getActualTypeArguments()
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
