package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.lang.reflect.ParameterizedType;
import java.util.List;

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
    public synchronized TypeID getRawType() {
        if (rawType != null)
            return rawType;
        return this.rawType = collectionGroup.of(parameterizedType.getRawType()).asType();
    }

    @Override
    public synchronized List<TypeOrTypeVariableID> getActualTypeArguments() {
        if (actualTypeArguments != null)
            return actualTypeArguments;
        return this.actualTypeArguments = collectionGroup.of(parameterizedType.getActualTypeArguments());
    }

    @Override
    public synchronized TypeID getOwnerType() {
        if (ownerType != null)
            return ownerType;

        if (parameterizedType.getOwnerType() == null)
            return null;

        return this.ownerType = collectionGroup.of(parameterizedType.getOwnerType()).asType();
    }
}
