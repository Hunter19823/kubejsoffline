package pie.ilikepiefoo.kubejsoffline.impl.identifier;

import pie.ilikepiefoo.kubejsoffline.api.identifier.ArrayBasedIndex;

public class ArrayIdentifier extends IdentifierBase implements ArrayBasedIndex {
    protected int arrayDepth;

    public ArrayIdentifier(int arrayIndex) {
        this(arrayIndex, 1);
    }

    public ArrayIdentifier(int arrayIndex, int arrayDepth) {
        super(arrayIndex);
    }

    @Override
    public int getArrayDepth() {
        return arrayDepth;
    }
}
