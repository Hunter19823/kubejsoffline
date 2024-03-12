package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class AnnotationsWrapper implements Annotations {
    private final NavigableMap<AnnotationID, String> annotations;
    private final Map<String, AnnotationID> identifiers;

    public AnnotationsWrapper() {
        this.annotations = new TreeMap<>();
        this.identifiers = new HashMap<>();
    }

    @Override
    public NavigableMap<AnnotationID, String> getAllAnnotations() {
        return annotations;
    }

    @Override
    public boolean contains(String annotation) {
        return identifiers.containsKey(annotation);
    }

    @Override
    public synchronized AnnotationID addAnnotation(String annotation) {
        if (identifiers.containsKey(annotation)) {
            return identifiers.get(annotation);
        }
        AnnotationID id = new AnnotationIdentifier(annotations.size());
        annotations.put(id, annotation);
        identifiers.put(annotation, id);
        return id;
    }

    public static class AnnotationIdentifier extends IdentifierBase implements AnnotationID {
        public AnnotationIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }

}
