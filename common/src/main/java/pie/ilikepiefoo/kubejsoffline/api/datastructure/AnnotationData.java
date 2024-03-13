package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.IndexedData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.AnnotationID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;

public interface AnnotationData extends IndexedData<AnnotationID> {
    TypeID getAnnotationType();

    String getAnnotationValue();
}
