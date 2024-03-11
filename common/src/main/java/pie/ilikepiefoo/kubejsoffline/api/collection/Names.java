package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;

import java.util.NavigableMap;

public interface Names {
    NavigableMap<NameID, String> getAllNames();

    boolean contains( String name );

    NameID addName(String name);
}
