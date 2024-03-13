package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

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
}
