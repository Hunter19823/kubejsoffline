package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

public interface WildcardTypeData {
    TypeOrTypeVariableID getExtends();

    TypeOrTypeVariableID getSuper();
}
