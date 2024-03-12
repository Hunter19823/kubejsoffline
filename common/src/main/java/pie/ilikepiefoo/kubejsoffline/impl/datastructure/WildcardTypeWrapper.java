package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.IndexedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.lang.reflect.WildcardType;

public class WildcardTypeWrapper implements WildcardTypeData {
    public WildcardTypeWrapper(CollectionGroup collectionGroup, WildcardType wildcardType) {
    }

    @Override
    public TypeID getIndex() {
        return null;
    }

    @Override
    public IndexedData<TypeID> setIndex(TypeID index) {
        return null;
    }

    @Override
    public TypeOrTypeVariableID getExtends() {
        return null;
    }

    @Override
    public TypeOrTypeVariableID getSuper() {
        return null;
    }
}
