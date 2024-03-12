package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface ParameterData extends AnnotatedData, NamedData, ModifierData, IndexedData<ParameterID> {
    TypeOrTypeVariableID getType();

}
