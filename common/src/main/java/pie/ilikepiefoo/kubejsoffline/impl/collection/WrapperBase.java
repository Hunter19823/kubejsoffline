package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.identifier.Index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public abstract class WrapperBase<INDEX extends Index, VALUE> {
    protected final NavigableMap<INDEX, VALUE> indexToValueMap = new TreeMap<>();
    protected final Map<VALUE, INDEX> valueToIndexMap = new HashMap<>();
    protected IndexFactory<INDEX> indexFactory;

    protected WrapperBase(IndexFactory<INDEX> indexFactory) {
        this.indexFactory = indexFactory;
    }

    public Collection<VALUE> getValues() {
        return indexToValueMap.values();
    }

    protected synchronized INDEX addValue(VALUE value) {
        if (valueToIndexMap.containsKey(value)) {
            return valueToIndexMap.get(value);
        }
        INDEX index = indexFactory.createIndex(indexToValueMap.size());
        indexToValueMap.put(index, value);
        valueToIndexMap.put(value, index);
        return index;
    }

    public boolean contains(VALUE value) {
        return valueToIndexMap.containsKey(value);
    }

    public int size() {
        return indexToValueMap.size();
    }

    public INDEX getIndex(VALUE value) {
        return valueToIndexMap.get(value);
    }

    public VALUE getValue(INDEX index) {
        return indexToValueMap.get(index);
    }

    protected interface IndexFactory<INDEX extends Index> {
        INDEX createIndex(int arrayIndex);
    }
}
