package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.IndexedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.lang.reflect.TypeVariable;
import java.util.List;

public class TypeVariableWrapper implements TypeVariableData {
    public TypeVariableWrapper(CollectionGroup collectionGroup, TypeVariable<?> typeVariable) {
    }

    @Override
    public TypeVariableID getIndex() {
        return null;
    }

    @Override
    public IndexedData<TypeID> setIndex(TypeID index) {
        return null;
    }

    @Override
    public NameID getName() {
        return null;
    }

    @Override
    public List<TypeOrTypeVariableID> getExtends() {
        return null;
    }

    @Override
    public List<TypeOrTypeVariableID> getSupers() {
        return null;
    }
}
