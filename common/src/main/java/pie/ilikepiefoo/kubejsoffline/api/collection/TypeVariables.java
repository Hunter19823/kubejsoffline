package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.api.identifier.TypeVariableID;

import java.lang.reflect.TypeVariable;
import java.util.NavigableMap;

public interface TypeVariables {
    NavigableMap<TypeVariableID, TypeVariableData> getAllTypeVariables();

    TypeVariableID addTypeVariable( TypeVariableData data );

    boolean contains( TypeVariableData data );

    TypeVariableID getID( TypeVariable<?> typeVariable );
}
