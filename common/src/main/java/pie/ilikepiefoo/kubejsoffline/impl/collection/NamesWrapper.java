package pie.ilikepiefoo.kubejsoffline.impl.collection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.NavigableMap;

public class NamesWrapper implements Names {
    protected final TwoWayMap<NameID, String> data = new TwoWayMap<>(NameIdentifier::new);

    @Override
    public NavigableMap<NameID, String> getAllNames() {
        return this.data.getIndexToValueMap();
    }

    @Override
    public boolean contains(String name) {
        return this.data.contains(name);
    }


    @Override
    public synchronized NameID addName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null!");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank!");
        }
        return this.data.add(name);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    @Override
    public JsonElement toJSON() {
        var json = new JsonArray();
        for (var value : this.data.getValues()) {
            json.add(value);
        }
        return json;
    }

    public static class NameIdentifier extends IdentifierBase implements NameID {
        public NameIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
