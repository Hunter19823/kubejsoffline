package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface WildcardTypeData extends IndexedData<TypeID>, TypeData {
    TypeOrTypeVariableID getExtends();

    TypeOrTypeVariableID getSuper();

    @Override
    default boolean isRawType() {
        return false;
    }

    @Override
    default boolean isParameterizedType() {
        return false;
    }

    @Override
    default boolean isWildcardType() {
        return true;
    }

    @Override
    default boolean isTypeVariable() {
        return false;
    }

    @Override
    default RawClassData asRawType() {
        throw new UnsupportedOperationException("Cannot cast WildcardTypeData to RawClassData");
    }

    @Override
    default ParameterizedTypeData asParameterizedType() {
        throw new UnsupportedOperationException("Cannot cast WildcardTypeData to ParameterizedTypeData");
    }

    @Override
    default WildcardTypeData asWildcardType() {
        return this;
    }

    @Override
    default TypeVariableData asTypeVariable() {
        throw new UnsupportedOperationException("Cannot cast WildcardTypeData to TypeVariableData");
    }
}
