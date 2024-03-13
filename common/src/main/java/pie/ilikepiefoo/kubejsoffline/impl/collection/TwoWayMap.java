package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.identifier.Index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class TwoWayMap<INDEX extends Index, VALUE> {
    protected final NavigableMap<INDEX, VALUE> indexToValueMap = new TreeMap<>();
    protected final Map<VALUE, INDEX> valueToIndexMap = new HashMap<>();
    protected IndexFactory<INDEX> indexFactory;

    protected TwoWayMap(IndexFactory<INDEX> indexFactory) {
        this.indexFactory = indexFactory;
    }

    public Collection<VALUE> getValues() {
        return indexToValueMap.values();
    }

    public Collection<INDEX> getIndexes() {
        return indexToValueMap.keySet();
    }

    public Map<VALUE, INDEX> getValueToIndexMap() {
        return valueToIndexMap;
    }

    public NavigableMap<INDEX, VALUE> getIndexToValueMap() {
        return indexToValueMap;
    }

    public IndexFactory<INDEX> getIndexFactory() {
        return indexFactory;
    }

    public synchronized INDEX add(VALUE value) {
        return add(value, indexFactory);
    }

    public synchronized INDEX add(VALUE value, IndexFactory<INDEX> indexFactory) {
        INDEX index = indexFactory.createIndex(indexToValueMap.size());
        if (valueToIndexMap.containsKey(value)) {
            return valueToIndexMap.get(value);
        }
        indexToValueMap.put(index, value);
        valueToIndexMap.put(value, index);
        return index;
    }

    public synchronized void put(INDEX index, VALUE value) {
        indexToValueMap.put(index, value);
        valueToIndexMap.put(value, index);
    }

    public boolean contains(VALUE value) {
        return valueToIndexMap.containsKey(value);
    }

    public boolean contains(INDEX index) {
        return indexToValueMap.containsKey(index);
    }

    public int size() {
        return indexToValueMap.size();
    }

    public INDEX get(VALUE value) {
        return valueToIndexMap.get(value);
    }

    public VALUE get(INDEX index) {
        return indexToValueMap.get(index);
    }

    public void clear() {
        indexToValueMap.clear();
        valueToIndexMap.clear();
    }

    public interface IndexFactory<INDEX extends Index> {
        INDEX createIndex(int arrayIndex);
    }
}
