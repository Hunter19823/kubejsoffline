package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.identifier.NameID;

import java.util.NavigableMap;

public interface Names extends JSONSerializable {
    NavigableMap<NameID, String> getAllNames();

    boolean contains(String name);

    NameID addName(String name);

    void clear();

}
