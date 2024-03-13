package pie.ilikepiefoo.kubejsoffline.impl.identifier;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
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
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof IdentifierBase) {
            return ((IdentifierBase) obj).arrayIndex == arrayIndex;
        }
        if (obj instanceof Index) {
            return ((Index) obj).getArrayIndex() == arrayIndex;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj) == arrayIndex;
        }
        return false;
    }

    @Override
    public JsonElement toJSON() {
        return new JsonPrimitive(arrayIndex);
    }
}
