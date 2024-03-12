package pie.ilikepiefoo.kubejsoffline.api.identifier;

public interface TypeOrTypeVariableID extends ArrayBasedIndex {
    default boolean isTypeVariable() {
        return false;
    }

    default boolean isType() {
        return false;
    }

    default TypeVariableID asTypeVariable() {
        throw new UnsupportedOperationException("Cannot cast to TypeVariableID.");
    }

    default TypeID asType() {
        throw new UnsupportedOperationException("Cannot cast to TypeID.");
    }
}