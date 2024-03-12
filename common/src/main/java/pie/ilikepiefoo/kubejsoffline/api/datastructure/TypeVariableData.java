package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public interface TypeVariableData extends NamedData, IndexedData<TypeVariableID>, TypeData {

    List<TypeOrTypeVariableID> getExtends();

    List<TypeOrTypeVariableID> getSupers();

    @Override
    default boolean isTypeVariable() {
        return true;
    }

    @Override
    default TypeVariableData asTypeVariable() {
        return this;
    }
}
