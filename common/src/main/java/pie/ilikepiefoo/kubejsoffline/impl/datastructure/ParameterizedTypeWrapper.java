package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.impl.TypeManager;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
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
    public ParameterizedTypeData setIndex(TypeID index) {
        this.index = index;
        return this;
    }

    @Override
    public synchronized TypeID getRawType() {
        if (rawType != null) {
            return rawType;
        }
        var rawType = TypeManager.INSTANCE.getID(parameterizedType.getRawType());
        if (!rawType.isType()) {
            throw new IllegalStateException("Raw Type is not a Type");
        }
        this.rawType = rawType.asType();

        return this.rawType;
    }

    @Override
    public synchronized List<TypeOrTypeVariableID> getActualTypeArguments() {
        if (actualTypeArguments != null) {
            return actualTypeArguments;
        }
        var typeArguments = parameterizedType.getActualTypeArguments();
        List<TypeOrTypeVariableID> actualTypeArguments = new LinkedList<>();
        for (Type typeArgument : typeArguments) {
            actualTypeArguments.add(TypeManager.INSTANCE.getID(typeArgument));
        }
        this.actualTypeArguments = actualTypeArguments;
        return this.actualTypeArguments;
    }

    @Override
    public synchronized TypeID getOwnerType() {
        if (ownerType != null) {
            return ownerType;
        }
        if (parameterizedType.getOwnerType() == null) {
            return null;
        }
        var ownerType = TypeManager.INSTANCE.getID(parameterizedType.getOwnerType());
        if (!ownerType.isType()) {
            throw new IllegalStateException("Owner Type is not a Type");
        }
        this.ownerType = ownerType.asType();
        return this.ownerType;
    }
}
