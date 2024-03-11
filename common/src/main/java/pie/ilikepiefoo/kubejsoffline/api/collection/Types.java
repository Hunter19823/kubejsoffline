package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeData;

import java.util.NavigableSet;

public interface Types {

    NavigableSet<TypeData> getAllTypes();

    void addType( TypeData data );

    boolean contains( TypeData data );
}
