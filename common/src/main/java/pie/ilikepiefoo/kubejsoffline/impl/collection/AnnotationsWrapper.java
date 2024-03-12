package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Annotations;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.NavigableMap;

public class AnnotationsWrapper extends WrapperBase<AnnotationID, String> implements Annotations {

    protected AnnotationsWrapper() {
        super(AnnotationIdentifier::new);
    }

    @Override
    public NavigableMap<AnnotationID, String> getAllAnnotations() {
        return this.indexToValueMap;
    }

    @Override
    public synchronized AnnotationID addAnnotation(String annotation) {
        return this.addValue(annotation);
    }

    public static class AnnotationIdentifier extends IdentifierBase implements AnnotationID {
        public AnnotationIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }

}
