package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.data.AnnotationData;

import java.util.NavigableMap;

public interface Annotations {
    NavigableMap<AnnotationID, AnnotationData> getAllAnnotations();

    boolean contains(AnnotationData annotation);

    AnnotationID addAnnotation(AnnotationData annotation);
}
