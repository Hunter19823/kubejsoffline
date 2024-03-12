package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.impl.TypeManager;

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
        if (!index.isType()) {
            throw new IllegalArgumentException("WildcardTypeData can only be indexed by TypeID");
        }
        this.index = index.asType();
        return this;
    }

    @Override
    public List<TypeOrTypeVariableID> getExtends() {
        if (extendsBounds != null) {
            return extendsBounds;
        }
        List<TypeOrTypeVariableID> extendsBounds = new java.util.LinkedList<>();
        for (Type type : wildcardType.getUpperBounds()) {
            if (type == Object.class) {
                continue;
            }
            extendsBounds.add(TypeManager.INSTANCE.getID(type));
        }
        return this.extendsBounds = extendsBounds;
    }

    @Override
    public List<TypeOrTypeVariableID> getSuper() {
        if (superBounds != null) {
            return superBounds;
        }
        List<TypeOrTypeVariableID> superBounds = new java.util.LinkedList<>();
        for (Type type : wildcardType.getLowerBounds()) {
            if (type == Object.class) {
                continue;
            }
            superBounds.add(TypeManager.INSTANCE.getID(type));
        }
        return this.superBounds = superBounds;
    }
}
