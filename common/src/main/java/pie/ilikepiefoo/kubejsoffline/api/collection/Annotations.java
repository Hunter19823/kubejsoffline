package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;

import java.util.NavigableMap;

public interface Annotations {
    NavigableMap<AnnotationID, String> getAllAnnotations();

    boolean contains( String annotation );

    AnnotationID addAnnotation(String annotation);
}
