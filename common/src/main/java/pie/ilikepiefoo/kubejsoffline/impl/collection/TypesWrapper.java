package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Types;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.ArrayIdentifier;

import java.util.NavigableMap;

public class TypesWrapper implements Types {
    protected TwoWayMap<TypeID, TypeData> data;
    protected NavigableMap<TypeID, RawClassData> rawTypes;
    protected NavigableMap<TypeID, ParameterizedTypeData> parameterizedTypes;
    protected NavigableMap<TypeID, WildcardTypeData> wildcardTypes;
    protected NavigableMap<TypeID, TypeData> typeVariables;


    public TypesWrapper() {
        this.data = new TwoWayMap<>(TypeIdentifier::new);
    }

    @Override
    public NavigableMap<TypeID, TypeData> getAllTypes() {
        return this.data.getIndexToValueMap();
    }

    @Override
    public NavigableMap<TypeID, RawClassData> getAllRawTypes() {
        return this.rawTypes;
    }

    @Override
    public NavigableMap<TypeID, ParameterizedTypeData> getAllParameterizedTypes() {
        return this.parameterizedTypes;
    }

    @Override
    public NavigableMap<TypeID, WildcardTypeData> getAllWildcardTypes() {
        return this.wildcardTypes;
    }

    @Override
    public NavigableMap<TypeID, TypeData> getAllTypeVariables() {
        return this.typeVariables;
    }

    @Override
    public synchronized TypeID addType(TypeData data) {
        if (this.data.contains(data)) {
            return this.data.get(data);
        }
        var index = this.data.add(data);
        if (data.isRawType()) {
            this.rawTypes.put(index, data.asRawType());
        } else if (data.isParameterizedType()) {
            this.parameterizedTypes.put(index, data.asParameterizedType());
        } else if (data.isWildcardType()) {
            this.wildcardTypes.put(index, data.asWildcardType());
        } else if (data.isTypeVariable()) {
            this.typeVariables.put(index, data);
        }
        return index;
    }

    @Override
    public boolean contains(TypeData data) {
        return this.data.contains(data);
    }

    @Override
    public TypeID getID(TypeData type) {
        if (type.isRawType()) {
            return getID(type.asRawType());
        } else if (type.isParameterizedType()) {
            return getID(type.asParameterizedType());
        } else if (type.isWildcardType()) {
            return getID(type.asWildcardType());
        } else if (type.isTypeVariable()) {
            return getID(type.asTypeVariable());
        }
        throw new UnsupportedOperationException("Unsupported TypeData.");
    }

    @Override
    public TypeID getID(RawClassData type) {
        return this.data.get(type);
    }

    @Override
    public TypeID getID(ParameterizedTypeData type) {
        return this.data.get(type);
    }

    @Override
    public TypeID getID(WildcardTypeData type) {
        return this.data.get(type);
    }

    @Override
    public TypeID getID(TypeVariableData type) {
        return this.data.get(type);
    }

    @Override
    public TypeData getType(TypeID id) {
        return this.data.get(id);
    }

    @Override
    public void clear() {
        this.data.clear();
        this.rawTypes.clear();
        this.parameterizedTypes.clear();
        this.wildcardTypes.clear();
        this.typeVariables.clear();
    }

    public static class TypeIdentifier extends ArrayIdentifier implements TypeID {

        public TypeIdentifier(int arrayIndex) {
            super(arrayIndex);
        }

        public TypeIdentifier(int arrayIndex, int arrayDepth) {
            super(arrayIndex, arrayDepth);
        }
    }
}
