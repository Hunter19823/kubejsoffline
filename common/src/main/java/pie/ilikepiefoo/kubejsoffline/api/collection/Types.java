package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.NavigableMap;

public interface Types {
    NavigableMap<TypeID, TypeData> getAllTypes();

    TypeID addType(TypeData data);

    boolean contains( TypeData data );

    TypeID getID(Class<?> clazz);

    TypeID getID(ParameterizedType type);

    TypeID getID(WildcardType type);
}
