package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.CollectionGroup;
import pie.ilikepiefoo.kubejsoffline.impl.TypeManager;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

public class TypeVariableWrapper implements TypeVariableData {
    protected final CollectionGroup collectionGroup;
    protected final TypeVariable<?> typeVariable;
    protected TypeVariableID index;
    protected NameID name;
    protected List<TypeOrTypeVariableID> bounds;

    public TypeVariableWrapper(CollectionGroup collectionGroup, TypeVariable<?> typeVariable) {
        this.collectionGroup = collectionGroup;
        this.typeVariable = typeVariable;
    }

    @Override
    public TypeVariableData setIndex(TypeOrTypeVariableID index) {
        if (!index.isTypeVariable()) {
            throw new IllegalArgumentException("TypeVariableData can only be indexed by TypeVariableID");
        }
        this.index = index.asTypeVariable();
        return this;
    }

    @Override
    public NameID getName() {
        if (name != null) {
            return name;
        }
        return name = collectionGroup.names().addName(typeVariable.getName());
    }

    @Override
    public List<TypeOrTypeVariableID> getBounds() {
        if (bounds != null) {
            return bounds;
        }
        List<TypeOrTypeVariableID> bounds = new java.util.LinkedList<>();
        for (Type type : typeVariable.getBounds()) {
            if (type == Object.class) {
                continue;
            }
            bounds.add(TypeManager.INSTANCE.getID(type));
        }
        return this.bounds = bounds;
    }

    @Override
    public TypeVariableID getIndex() {
        return index;
    }
}
