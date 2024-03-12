package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public interface TypeVariableData extends NamedData, IndexedData<TypeVariableID> {

    List<TypeOrTypeVariableID> getExtends();

    List<TypeOrTypeVariableID> getSupers();
}
