package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PackageJSONManager {
	private static final Logger LOG = LogManager.getLogger();
	private static PackageJSONManager INSTANCE;
	private final AtomicInteger packageIDs;
	private JsonArray packageData;
	private ConcurrentHashMap<String, Integer> packageMap;

	private PackageJSONManager() {
		this.packageData = new JsonArray();
		this.packageIDs = new AtomicInteger(-1);
		this.packageMap = new ConcurrentHashMap<>();
	}

	public static PackageJSONManager getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new PackageJSONManager();
		}
		return INSTANCE;
	}

	@Nonnull
	public static String getParentPackage(@Nonnull String packageName) {
		if (packageName.isBlank()) {
			return "";
		}
		// Substring the last period from the package name.
		// This will give us the parent package name.
		return packageName.substring(0, packageName.lastIndexOf('.'));
	}

	@Nonnull
	public static String getCurrentPackage(@Nonnull String packageName) {
		if (packageName.isBlank()) {
			return "";
		}
		// Substring from last period.
		// This will give us the current package name.
		return packageName.substring(packageName.lastIndexOf('.') + 1);
	}

	public synchronized void clear() {
		this.packageMap.clear();
		this.packageData = new JsonArray();
		this.packageIDs.set(-1);
		this.packageMap = new ConcurrentHashMap<>();
	}

	@Nonnull
	public Integer getPackageID(@Nonnull final String packageName) {
		if (packageName.isBlank()) {
			return getPackageID("java.lang");
		}
		// Check without synchronization first.
		if (packageMap.containsKey(packageName)) {
			return packageMap.get(packageName);
		}
		synchronized (this) {
			// Check again with synchronization.
			// This is to prevent multiple threads from creating the same package if they both check at the same time.
			if (packageMap.containsKey(packageName)) {
				return packageMap.get(packageName);
			}
			// Check if the package name contains a period.
			var containsParent = packageName.contains(".");
			// If this is a parent package, then we will add it to the package map.
			if (!containsParent) {
				this.packageData.add(packageName);
				final var id = this.packageIDs.incrementAndGet();
				this.packageMap.put(packageName, id);
				return id;
			}
			// If this is not a parent package then we need to make a new json array
			// containing the parent package id and the last part of the package name.
			final var parentPackage = getParentPackage(packageName);
			final var parentID = this.getPackageID(parentPackage);
			final var packageArray = new JsonArray();
			final var id = this.packageIDs.incrementAndGet();
			packageArray.add(parentID);
			packageArray.add(getCurrentPackage(packageName));
			this.packageData.add(packageArray);
			this.packageMap.put(packageName, id);
			return id;
		}
	}

	@Nonnull
	public synchronized JsonArray getPackageData() {
		return this.packageData;
	}

	public void attachPackageData(@Nonnull final JsonObject typeData, @Nonnull final Class<?> type) {
		final var packageName = SafeOperations.tryGet(type::getPackageName);
		packageName.ifPresent((name) -> {
			final var packageID = this.getPackageID(name);
			typeData.addProperty(JSONProperty.PACKAGE_NAME.jsName, packageID);
			if (typeData.has(JSONProperty.TYPE_IDENTIFIER.jsName)) {
				// If the type identifier is a qualified name, remove the package name and starting period from it.
				var qualifiedName = typeData.get(JSONProperty.TYPE_IDENTIFIER.jsName).getAsString();
				if (qualifiedName.startsWith(name)) {
					qualifiedName = qualifiedName.substring(name.length() + 1);
					typeData.addProperty(JSONProperty.TYPE_IDENTIFIER.jsName, qualifiedName);
				}
			}
		});
	}
}
