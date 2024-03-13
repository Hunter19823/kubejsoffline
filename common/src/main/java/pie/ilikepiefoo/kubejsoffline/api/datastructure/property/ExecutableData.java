package pie.ilikepiefoo.kubejsoffline.api.datastructure.property;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public interface ExecutableData extends JSONSerializable {
    List<TypeVariableID> getTypeParameters();

    List<TypeID> getExceptions();

    List<ParameterID> getParameters();
}
