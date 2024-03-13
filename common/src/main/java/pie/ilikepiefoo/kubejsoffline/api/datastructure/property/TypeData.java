package pie.ilikepiefoo.kubejsoffline.api.datastructure.property;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface TypeData extends IndexedData<TypeOrTypeVariableID> {
    default boolean isRawType() {
        return false;
    }

    default boolean isParameterizedType() {
        return false;
    }

    default boolean isWildcardType() {
        return false;
    }

    default boolean isTypeVariable() {
        return false;
    }

    default RawClassData asRawType() {
        throw new UnsupportedOperationException("Cannot cast to RawClassData.");
    }

    default ParameterizedTypeData asParameterizedType() {
        throw new UnsupportedOperationException("Cannot cast to ParameterizedTypeData.");
    }

    default WildcardTypeData asWildcardType() {
        throw new UnsupportedOperationException("Cannot cast to WildcardTypeData.");
    }

    default TypeVariableData asTypeVariable() {
        throw new UnsupportedOperationException("Cannot cast to TypeVariableData.");
    }
}
