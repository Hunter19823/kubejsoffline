package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import org.checkerframework.checker.nullness.qual.Nullable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;

public class PackagePartWrapper implements PackagePart {
    protected String name;
    @Nullable
    protected PackageID prefix;

    public PackagePartWrapper(String name) {
        this(null, name);
    }

    public PackagePartWrapper(@Nullable PackageID prefix, String name) {
        this.name = name;
        this.prefix = prefix;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public @Nullable PackageID getPrefix() {
        return null;
    }
}
