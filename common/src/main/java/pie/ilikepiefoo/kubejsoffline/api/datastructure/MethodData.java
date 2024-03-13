package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.AnnotatedData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.ExecutableData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.ModifierData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.NamedData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface MethodData extends AnnotatedData, ExecutableData, NamedData, ModifierData {
    TypeOrTypeVariableID getType();
}
