package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.data.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.NavigableMap;

public class AnnotationsWrapper implements Annotations {
    protected final TwoWayMap<AnnotationID, AnnotationData> data = new TwoWayMap<>(AnnotationIdentifier::new);

    @Override
    public NavigableMap<AnnotationID, AnnotationData> getAllAnnotations() {
        return this.data.getIndexToValueMap();
    }

    @Override
    public boolean contains(AnnotationData annotation) {
        return this.data.contains(annotation);
    }

    @Override
    public synchronized AnnotationID addAnnotation(AnnotationData annotation) {
        return this.data.add(annotation);
    }

    public static class AnnotationIdentifier extends IdentifierBase implements AnnotationID {
        public AnnotationIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }

}
