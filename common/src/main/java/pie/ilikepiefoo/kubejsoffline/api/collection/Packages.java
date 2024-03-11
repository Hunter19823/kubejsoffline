package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;

import java.util.NavigableSet;

public interface Packages {

    NavigableSet<PackagePart> getAllPackages();

    PackagePart addPackage( String packageName );

    boolean contains( String packageName );

    boolean contains( PackagePart packageName );


}
