package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.impl.TypeManager;

import java.lang.annotation.Annotation;

public class AnnotationDataWrapper implements AnnotationData {

    protected Annotation annotation;
    protected TypeID annotationType;
    protected CollectionGroup collectionGroup;
    protected AnnotationID index;

    public AnnotationDataWrapper(CollectionGroup collectionGroup, Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public TypeID getAnnotationType() {
        if (annotationType != null) {
            return annotationType;
        }
        return annotationType = TypeManager.INSTANCE.getID(annotation.annotationType()).asType();
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
