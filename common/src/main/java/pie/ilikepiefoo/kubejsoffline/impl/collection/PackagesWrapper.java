package pie.ilikepiefoo.kubejsoffline.impl.collection;

import pie.ilikepiefoo.kubejsoffline.api.collection.Packages;
import pie.ilikepiefoo.kubejsoffline.api.datastructure.PackagePart;
import pie.ilikepiefoo.kubejsoffline.api.identifier.PackageID;
import pie.ilikepiefoo.kubejsoffline.impl.datastructure.PackagePartWrapper;
import pie.ilikepiefoo.kubejsoffline.impl.identifier.IdentifierBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.StringJoiner;
import java.util.TreeMap;

public class PackagesWrapper implements Packages {
    protected NavigableMap<PackageID, PackagePart> packages;
    protected Map<String, PackageID> parts;

    public PackagesWrapper() {
        this.packages = new TreeMap<>();
        this.parts = new HashMap<>();
    }

    @Override
    public NavigableMap<PackageID, PackagePart> getAllPackages() {
        return packages;
    }

    @Override
    public synchronized PackageID addPackage(String packageName) {
        if (parts.containsKey(packageName)) {
            return parts.get(packageName);
        }
        var lastPeriod = packageName.lastIndexOf(".");
        if (lastPeriod == -1) {
            PackageID id = new PackageIdentifier(packages.size());
            PackagePart part = new PackagePartWrapper(packageName);
            packages.put(id, part);
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
        PackageID id = new PackageIdentifier(packages.size());
        PackagePart part = new PackagePartWrapper(prefixID, name);
        packages.put(id, part);
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
        // Get all prefixes, then join them together.
        // Ensure that the prefix goes first.
        // Cannot use a StringJoiner because it cannot be reversed.
        ArrayList<String> parts = new ArrayList<>();
        PackagePart current = part;
        while (current != null) {
            parts.add(current.getName());
            current = getPackage(current.getPrefix());
        }
        StringJoiner joiner = new StringJoiner(".");
        for (int i = parts.size() - 1; i >= 0; i--) {
            joiner.add(parts.get(i));
        }
        return getID(joiner.toString());
    }

    @Override
    public PackagePart getPackage(PackageID id) {
        return null;
    }

    @Override
    public PackagePart getPackage(String packageName) {
        return null;
    }

    public static class PackageIdentifier extends IdentifierBase implements PackageID {
        public PackageIdentifier(int arrayIndex) {
            super(arrayIndex);
        }
    }
}
