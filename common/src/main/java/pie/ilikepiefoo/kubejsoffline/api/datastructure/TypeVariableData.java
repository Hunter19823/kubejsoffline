package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

import java.util.List;

public interface TypeVariableData extends NamedData {

    List<TypeOrTypeVariableID> getExtends();

    List<TypeOrTypeVariableID> getSupers();
}
