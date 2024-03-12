package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

import java.util.List;

public interface ParameterizedTypeData {
    TypeID getRawType();

    List<TypeOrTypeVariableID> getActualTypeArguments();
}
