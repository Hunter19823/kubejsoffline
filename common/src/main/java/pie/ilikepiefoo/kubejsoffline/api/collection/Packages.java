package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;

import java.util.NavigableMap;

public interface Packages {

    NavigableMap<PackageID, PackagePart> getAllPackages();

    PackageID addPackage(String packageName);

    boolean contains( String packageName );


}
