package pie.ilikepiefoo.kubejsoffline.api.collection;

import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;

import java.util.NavigableMap;

public interface Packages extends JSONSerializable {

    NavigableMap<PackageID, PackagePart> getAllPackages();

    PackageID addPackage(String packageName);

    boolean contains(String packageName);

    PackageID getID(String packageName);

    PackageID getID(PackagePart part);

    PackagePart getPackage(PackageID id);

    PackagePart getPackage(String packageName);

    void clear();

}
