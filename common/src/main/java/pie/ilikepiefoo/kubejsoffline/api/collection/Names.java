package pie.ilikepiefoo.kubejsoffline.api.collection;

import java.util.NavigableSet;

public interface Names {
    NavigableSet<String> getAllNames();

    boolean contains( String name );

    boolean addName( String name );
}
