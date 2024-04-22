package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.util.json.JSONProperty;

import java.lang.annotation.Annotation;
import java.util.Objects;

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
    public AnnotationID getIndex() {
        return index;
    }

    @Override
    public AnnotationData setIndex(AnnotationID index) {
        this.index = index;
        return this;
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonObject();
        json.add(JSONProperty.ANNOTATION_TYPE.jsName, getAnnotationType().toJSON());
        if (!getAnnotationValue().isBlank()) {
            json.addProperty(JSONProperty.ANNOTATION_STRING.jsName, getAnnotationValue());
        }
        return json;
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
        var value = annotation.toString();
        // Substring from first and last parenthesis.
        int start = value.indexOf('(');
        int end = value.lastIndexOf(')');
        if (start != -1 && end != -1) {
            return value.substring(start + 1, end);
        }
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getAnnotationType(),
                getAnnotationValue()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return this.hashCode() == obj.hashCode();
    }
}
