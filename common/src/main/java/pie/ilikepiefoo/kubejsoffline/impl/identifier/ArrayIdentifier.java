package pie.ilikepiefoo.kubejsoffline.impl.identifier;

import pie.ilikepiefoo.kubejsoffline.api.identifier.ArrayBasedIndex;
import pie.ilikepiefoo.kubejsoffline.api.identifier.Index;

public class ArrayIdentifier extends IdentifierBase implements ArrayBasedIndex {
    protected int arrayDepth;

    public ArrayIdentifier(int arrayIndex) {
        this(arrayIndex, 1);
    }

    public ArrayIdentifier(int arrayIndex, int arrayDepth) {
        super(arrayIndex);
    }

    public ArrayIdentifier(Index index, int arrayDepth) {
        this(index.getArrayIndex(), arrayDepth);
    }

    public ArrayIdentifier(ArrayBasedIndex index, int arrayDepth) {
        this(index.getArrayIndex(), arrayDepth + index.getArrayDepth());
    }

    @Override
    public int getArrayDepth() {
        return arrayDepth;
    }
}
