package pie.ilikepiefoo.kubejsoffline.impl.identifier;

import pie.ilikepiefoo.kubejsoffline.api.identifier.Index;

public class IdentifierBase implements Index {

    protected int arrayIndex;

    public IdentifierBase(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    @Override
    public int getArrayIndex() {
        return arrayIndex;
    }

    public void setArrayIndex(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(arrayIndex);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Index) {
            return ((Index) obj).getArrayIndex() == arrayIndex;
        }
        return false;
    }
}
