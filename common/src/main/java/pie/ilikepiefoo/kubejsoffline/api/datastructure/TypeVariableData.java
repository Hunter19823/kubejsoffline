package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

import java.util.List;

public interface TypeVariableData {
    NameID getName();

    List<TypeOrTypeVariableID> getExtends();

    List<TypeOrTypeVariableID> getSupers();
}
