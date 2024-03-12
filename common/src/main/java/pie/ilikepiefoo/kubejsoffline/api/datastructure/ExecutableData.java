package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public interface ExecutableData {
    List<TypeVariableID> getTypeParameters();

    List<TypeID> getExceptions();

    List<ParameterID> getParameters();
}
