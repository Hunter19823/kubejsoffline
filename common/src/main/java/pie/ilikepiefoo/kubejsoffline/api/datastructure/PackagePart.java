package pie.ilikepiefoo.kubejsoffline.api.datastructure;

import org.checkerframework.checker.nullness.qual.Nullable;
import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;

public interface PackagePart {

    String getName();

    @Nullable PackageID getPrefix();

    default String getFullName(Packages packages) {
        if (getPrefix() != null) {
            return packages.getPackage(getPrefix()).getFullName(packages) + "." + getName();
        }
        return getName();
    }
}