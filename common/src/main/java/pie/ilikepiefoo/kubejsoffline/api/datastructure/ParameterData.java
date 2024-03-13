package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.AnnotatedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.IndexedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.ModifierData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.NamedData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface ParameterData extends AnnotatedData, NamedData, ModifierData, IndexedData<ParameterID> {
    TypeOrTypeVariableID getType();

}
