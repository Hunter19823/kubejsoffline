package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface MethodData extends AnnotatedData, ExecutableData, NamedData {
    TypeOrTypeVariableID getType();
}
