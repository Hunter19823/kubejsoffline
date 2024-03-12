package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface WildcardTypeData extends IndexedData<TypeID>, TypeData {
    TypeOrTypeVariableID getExtends();

    TypeOrTypeVariableID getSuper();

    @Override
    default boolean isWildcardType() {
        return true;
    }

    @Override
    default WildcardTypeData asWildcardType() {
        return this;
    }
}
