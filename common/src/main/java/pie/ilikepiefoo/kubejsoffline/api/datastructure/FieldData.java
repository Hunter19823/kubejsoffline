package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface FieldData extends ModifierData, AnnotatedData, NamedData {
    TypeOrTypeVariableID getType();
}
