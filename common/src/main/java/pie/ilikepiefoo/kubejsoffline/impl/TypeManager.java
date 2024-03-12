package pie.ilikepiefoo.kubejsoffline.impl;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.ParameterizedTypeWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.RawClassWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.TypeVariableWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.WildcardTypeWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.ArrayIdentifier;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeManager {
    public static final TypeManager INSTANCE = new TypeManager(CollectionGroup.INSTANCE);
    protected final Map<Type, TypeOrTypeVariableID> cache = new HashMap<>();

    protected final CollectionGroup collectionGroup;

    public TypeManager(CollectionGroup group) {
        this.collectionGroup = group;
    }

    public synchronized TypeOrTypeVariableID getID(final Type type) {
        if (type == null) {
            throw new NullPointerException("Type cannot be null");
        }
        if (cache.containsKey(type)) {
            return cache.get(type);
        }
        int arrayDepth = 0;
        var currentType = type;
        while (currentType instanceof Class<?> clazz && clazz.isArray()) {
            arrayDepth++;
            currentType = clazz.getComponentType();
        }
        while (currentType instanceof GenericArrayType genericArrayType) {
            arrayDepth++;
            currentType = genericArrayType.getGenericComponentType();
        }
        if (arrayDepth > 0) {
            return cache(type, new TypeIdentifier(getID(currentType), arrayDepth));
        }
        // Raw Type
        if (type instanceof Class<?> clazz) {
            return cache(clazz, new RawClassWrapper(collectionGroup, clazz));
        }
        // Parameterized Type
        if (type instanceof ParameterizedType parameterizedType) {
            return cache(parameterizedType, new ParameterizedTypeWrapper(collectionGroup, parameterizedType));
        }
        // WildcardType
        if (type instanceof java.lang.reflect.WildcardType wildcardType) {
            return cache(wildcardType, new WildcardTypeWrapper(collectionGroup, wildcardType));
        }
        // TypeVariable
        if (type instanceof java.lang.reflect.TypeVariable<?> typeVariable) {
            return cache(typeVariable, new TypeVariableWrapper(collectionGroup, typeVariable));
        }
        throw new IllegalArgumentException("Type " + type + " is not supported");
    }

    private TypeID cache(Type type, TypeData data) {
        var id = collectionGroup.types().addType(data);
        cache.put(type, id);
        data.setIndex(id);
        return id;
    }

    private TypeID cache(Type type, TypeID id) {
        cache.put(type, id);
        return id;
    }

    public static class TypeIdentifier extends ArrayIdentifier implements TypeID {

        public TypeIdentifier(int arrayIndex) {
            super(arrayIndex);
        }

        public TypeIdentifier(int arrayIndex, int arrayDepth) {
            super(arrayIndex, arrayDepth);
        }

        public TypeIdentifier(TypeID typeID, int arrayDepth) {
            super(typeID, arrayDepth);
        }
    }
}
