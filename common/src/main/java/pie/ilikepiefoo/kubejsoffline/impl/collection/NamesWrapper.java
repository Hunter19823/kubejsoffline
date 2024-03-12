package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class NamesWrapper implements Names {
    protected NavigableMap<NameID, String> names;
    protected Map<String, NameID> identifiers;

    public NamesWrapper() {
        this.names = new TreeMap<>();
        this.identifiers = new HashMap<>();
    }

    @Override
    public NavigableMap<NameID, String> getAllNames() {
        return names;
    }

    @Override
    public boolean contains(String name) {
        return identifiers.containsKey(name);
    }

    @Override
    public synchronized NameID addName(String name) {
        if (identifiers.containsKey(name)) {
            return identifiers.get(name);
        }
        NameID id = new NameIdentifier(names.size());
        names.put(id, name);
        identifiers.put(name, id);
        return id;
    }

    public static class NameIdentifier extends IdentifierBase implements NameID {
        public NameIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
