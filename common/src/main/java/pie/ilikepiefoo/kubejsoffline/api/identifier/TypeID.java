package pie.ilikepiefoo.kubejsoffline.api.identifier;

public interface TypeID extends TypeOrTypeVariableID {

    @Override
    default boolean isType() {
        return true;
    }

    @Override
    default TypeID asType() {
        return this;
    }
}