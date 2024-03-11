package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeID;

import java.util.NavigableMap;

public interface Types {
    NavigableMap<TypeID, TypeData> getAllTypes();

    TypeID addType(TypeData data);

    boolean contains( TypeData data );
}
