package pie.ilikepiefoo.kubejsoffline.api.identifier;

public interface TypeOrTypeVariableID extends ArrayBasedIndex {
    boolean isTypeVariable();

    boolean isType();
}