package pie.ilikepiefoo.kubejsoffline.api.collection;

import java.util.NavigableSet;

public interface Annotations {
    NavigableSet<String> getAllAnnotations();

    boolean contains( String annotation );

    boolean addAnnotation( String annotation );
}
