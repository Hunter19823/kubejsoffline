package pie.ilikepiefoo.kubejsoffline.api.datastructure.property;

import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;

import java.util.List;

public interface AnnotatedData {
    List<AnnotationID> getAnnotations();
}
