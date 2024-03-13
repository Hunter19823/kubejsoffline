package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.ParameterizedTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.RawClassData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.WildcardTypeData;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.property.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeOrTypeVariableID;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.util.NavigableMap;

public interface Types extends JSONSerializable {
    NavigableMap<TypeOrTypeVariableID, TypeData> getAllTypes();

    NavigableMap<TypeID, RawClassData> getAllRawTypes();

    NavigableMap<TypeID, ParameterizedTypeData> getAllParameterizedTypes();

    NavigableMap<TypeID, WildcardTypeData> getAllWildcardTypes();

    NavigableMap<TypeVariableID, TypeData> getAllTypeVariables();

    TypeOrTypeVariableID addType(TypeData data);

    boolean contains(TypeData data);

    TypeOrTypeVariableID getID(TypeData type);

    TypeID getID(RawClassData type);

    TypeID getID(ParameterizedTypeData type);

    TypeID getID(WildcardTypeData type);

    TypeVariableID getID(TypeVariableData type);

    TypeData getType(TypeID id);

    void clear();
}
