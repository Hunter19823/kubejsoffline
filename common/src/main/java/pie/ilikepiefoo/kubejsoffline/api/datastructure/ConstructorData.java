package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.ParameterID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.List;

public interface ConstructorData {
    int getModifiers();

    List<AnnotationID> getAnnotations();

    List<TypeVariableID> getTypeParameters();

    List<TypeID> getExceptions();

    List<ParameterID> getParameters();
}
