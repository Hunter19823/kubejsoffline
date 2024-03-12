package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Names;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.NavigableMap;

public class NamesWrapper extends WrapperBase<NameID, String> implements Names {
    protected NamesWrapper() {
        super(NameIdentifier::new);
    }

    @Override
    public NavigableMap<NameID, String> getAllNames() {
        return this.indexToValueMap;
    }


    @Override
    public synchronized NameID addName(String name) {
        return this.addValue(name);
    }

    public static class NameIdentifier extends IdentifierBase implements NameID {
        public NameIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
