package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;

import java.lang.annotation.Annotation;

public class AnnotationWrapper implements AnnotationData {

    protected Annotation annotation;
    protected TypeID annotationType;
    protected CollectionGroup collectionGroup;
    protected AnnotationID index;

    public AnnotationWrapper(CollectionGroup collectionGroup, Annotation annotation) {
        this.annotation = annotation;
        this.collectionGroup = collectionGroup;
    }

    @Override
    public TypeID getAnnotationType() {
        if (annotationType != null) {
            return annotationType;
        }
        return annotationType = collectionGroup.of(annotation.annotationType()).asType();
    }

    @Override
    public String getAnnotationValue() {
        return annotation.toString();
    }

    @Override
    public AnnotationID getIndex() {
        return index;
    }

    @Override
    public AnnotationData setIndex(AnnotationID index) {
        this.index = index;
        return this;
    }
}
