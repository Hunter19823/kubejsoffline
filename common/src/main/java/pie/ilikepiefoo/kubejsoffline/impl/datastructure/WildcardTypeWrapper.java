package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Objects;

public class WildcardTypeWrapper implements WildcardTypeData {
    protected final CollectionGroup collectionGroup;
    protected final WildcardType wildcardType;
    protected TypeID index;
    protected List<TypeOrTypeVariableID> extendsBounds;
    protected List<TypeOrTypeVariableID> superBounds;


    public WildcardTypeWrapper(CollectionGroup collectionGroup, WildcardType wildcardType) {
        this.collectionGroup = collectionGroup;
        this.wildcardType = wildcardType;
    }

    @Override
    public TypeID getIndex() {
        return index;
    }

    @Override
    public WildcardTypeData setIndex(TypeOrTypeVariableID index) {
        this.index = index.asType();
        return this;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        if (getSuper().isEmpty() && getExtends().isEmpty()) {
            return json;
        }
        if (!getSuper().isEmpty()) {
            json.add(JSONProperty.WILDCARD_LOWER_BOUNDS.jsName, JSONSerializable.of(getSuper()));
        }
        if (!getExtends().isEmpty()) {
            json.add(JSONProperty.WILDCARD_UPPER_BOUNDS.jsName, JSONSerializable.of(getExtends()));
        }
        return json;
    }

    @Override
    public synchronized List<TypeOrTypeVariableID> getExtends() {
        if (extendsBounds != null) {
            return extendsBounds;
        }
        return this.extendsBounds = collectionGroup.of(wildcardType.getUpperBounds(), (Type type) -> type == Object.class);
    }

    @Override
    public synchronized List<TypeOrTypeVariableID> getSuper() {
        if (superBounds != null) {
            return superBounds;
        }
        return this.superBounds = collectionGroup.of(wildcardType.getLowerBounds(), (Type type) -> type == Object.class);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getExtends(),
                getSuper()
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
