package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.identifier.Index;

public interface IndexedData<INDEX_TYPE extends Index> {
    INDEX_TYPE getIndex();
}
