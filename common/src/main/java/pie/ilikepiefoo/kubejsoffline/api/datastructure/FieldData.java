package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

import java.util.List;

public interface FieldData {
    NameID getName();

    int getModifiers();

    TypeOrTypeVariableID getType();

    List<AnnotationID> getAnnotations();
}
