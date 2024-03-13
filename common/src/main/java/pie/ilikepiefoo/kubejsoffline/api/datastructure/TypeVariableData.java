package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.NamedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public interface TypeVariableData extends NamedData, TypeData {

    List<TypeOrTypeVariableID> getBounds();

    @Override
    default boolean isTypeVariable() {
        return true;
    }

    @Override
    default TypeVariableData asTypeVariable() {
        return this;
    }

    @Override
    TypeVariableID getIndex();
}
