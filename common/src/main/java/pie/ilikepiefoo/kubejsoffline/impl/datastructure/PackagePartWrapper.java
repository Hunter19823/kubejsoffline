package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import org.checkerframework.checker.nullness.qual.Nullable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;

public class PackagePartWrapper implements PackagePart {
    protected PackageID index;
    protected String name;
    @Nullable
    protected PackageID prefix;

    public PackagePartWrapper(String name, PackageID index) {
        this(null, name, index);
    }

    public PackagePartWrapper(@Nullable PackageID prefix, String name, PackageID index) {
        this.index = index;
        this.name = name;
        this.prefix = prefix;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public @Nullable PackageID getPrefix() {
        return this.prefix;
    }

    @Override
    public PackageID getIndex() {
        return this.index;
    }

    @Override
    public PackagePart setIndex(PackageID index) {
        this.index = index;
        return this;
    }
}
