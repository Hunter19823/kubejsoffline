package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;

import java.util.NavigableMap;

public interface Types {
    NavigableMap<TypeID, TypeData> getAllTypes();

    NavigableMap<TypeID, RawClassData> getAllRawTypes();

    NavigableMap<TypeID, ParameterizedTypeData> getAllParameterizedTypes();

    NavigableMap<TypeID, WildcardTypeData> getAllWildcardTypes();

    NavigableMap<TypeID, TypeData> getAllTypeVariables();

    TypeID addType(TypeData data);

    boolean contains(TypeData data);

    TypeID getID(TypeData type);

    TypeID getID(RawClassData type);

    TypeID getID(ParameterizedTypeData type);

    TypeID getID(WildcardTypeData type);

    TypeID getID(TypeVariableData type);

    TypeData getType(TypeID id);

    void clear();
}
