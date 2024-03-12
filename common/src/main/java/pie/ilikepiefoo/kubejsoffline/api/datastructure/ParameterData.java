package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;

import java.util.List;

public interface ParameterData {
    int getModifiers();

    String getName();

    TypeOrTypeVariableID getType();

    List<AnnotationID> getAnnotations();
}
