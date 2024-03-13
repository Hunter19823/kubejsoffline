package pie.ilikepiefoo.kubejsoffline.impl.datastructure;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.checkerframework.checker.nullness.qual.Nullable;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;

import java.util.Objects;

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

    @Override
    public JsonElement toJSON() {
        var json = new JsonArray();
        json.add(new JsonPrimitive(getName()));
        if (getPrefix() != null) {
            json.add(getPrefix().toJSON());
        }
        return json;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getName(),
                getPrefix()
        );
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        return this.hashCode() == obj.hashCode();
    }
}
