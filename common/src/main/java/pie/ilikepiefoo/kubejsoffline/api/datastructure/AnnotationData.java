package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;

public interface AnnotationData {
    TypeID getAnnotationType();

    String getAnnotationValue();
}
