package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;

import java.util.NavigableMap;

public interface Annotations extends JSONSerializable {
    NavigableMap<AnnotationID, AnnotationData> getAllAnnotations();

    boolean contains(AnnotationData annotation);

    AnnotationID addAnnotation(AnnotationData annotation);

    void clear();
}
