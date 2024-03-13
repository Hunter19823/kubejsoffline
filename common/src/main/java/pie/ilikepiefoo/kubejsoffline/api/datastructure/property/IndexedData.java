package pie.ilikepiefoo.kubejsoffline.api.datastructure.property;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.identifier.Index;

public interface IndexedData<INDEX_TYPE extends Index> extends JSONSerializable {
    INDEX_TYPE getIndex();

    IndexedData<INDEX_TYPE> setIndex(INDEX_TYPE index);
}
