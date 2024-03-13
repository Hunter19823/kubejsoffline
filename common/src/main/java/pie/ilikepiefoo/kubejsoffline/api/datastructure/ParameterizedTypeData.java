package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

import java.util.List;

public interface ParameterizedTypeData extends TypeData {
    TypeID getRawType();

    List<TypeOrTypeVariableID> getActualTypeArguments();

    TypeID getOwnerType();


    @Override
    default boolean isParameterizedType() {
        return true;
    }

    @Override
    default ParameterizedTypeData asParameterizedType() {
        return this;
    }

}
