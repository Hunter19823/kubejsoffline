package pie.ilikepiefoo.kubejsoffline.impl.collection;

import com.google.gson.JsonElement;
import pie.ilikepiefoo.kubejsoffline.api.JSONSerializable;
import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.PackagePartWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

public class PackagesWrapper implements Packages {
    protected final TwoWayMap<PackageID, PackagePart> data = new TwoWayMap<>(PackageIdentifier::new);
    protected Map<String, PackageID> parts = new HashMap<>();

    @Override
    public NavigableMap<PackageID, PackagePart> getAllPackages() {
        return this.data.getIndexToValueMap();
    }

    @Override
    public synchronized PackageID addPackage(String packageName) {
        if (parts.containsKey(packageName)) {
            return parts.get(packageName);
        }
        var lastPeriod = packageName.lastIndexOf(".");
        if (lastPeriod == -1) {
            PackageID id = this.data.getIndexFactory().createIndex(this.data.size());
            PackagePart part = new PackagePartWrapper(packageName, id);
            this.data.put(id, part);
            parts.put(packageName, id);
            return id;
        }

        String prefix = packageName.substring(0, lastPeriod);
        String name = packageName.substring(lastPeriod + 1);
        if (prefix.isBlank()) {
            throw new IllegalArgumentException("Package names cannot start with a period.");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Package names cannot end with a period.");
        }
        PackageID prefixID = addPackage(prefix);
        PackageID id = this.data.getIndexFactory().createIndex(this.data.size());
        PackagePart part = new PackagePartWrapper(prefixID, name, id);
        this.data.put(id, part);
        parts.put(packageName, id);
        return id;
    }

    @Override
    public boolean contains(String packageName) {
        return parts.containsKey(packageName);
    }

    @Override
    public PackageID getID(String packageName) {
        return parts.get(packageName);
    }

    @Override
    public PackageID getID(PackagePart part) {
        return part.getIndex();
    }

    @Override
    public PackagePart getPackage(PackageID id) {
        return this.data.get(id);
    }

    @Override
    public PackagePart getPackage(String packageName) {
        return getPackage(getID(packageName));
    }

    @Override
    public void clear() {
        this.data.clear();
        this.parts.clear();
    }

    @Override
    public JsonElement toJSON() {
        return JSONSerializable.of(this.data.getValues());
    }

    public static class PackageIdentifier extends IdentifierBase implements PackageID {
        public PackageIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
