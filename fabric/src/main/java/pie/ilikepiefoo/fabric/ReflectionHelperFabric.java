package pie.ilikepiefoo.fabric;

import dev.latvian.mods.kubejs.event.EventJS;
import net.fabricmc.loader.api.FabricLoader;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import pie.ilikepiefoo.util.ReflectionHelper;

import java.nio.file.Path;
import java.util.Arrays;

public class ReflectionHelperFabric implements ReflectionHelper {
	@Override
	public Class[] getClasses() {
		Package[] packages = Package.getPackages();
		String[] packageNames = Arrays.stream(packages).parallel().map(Package::getName).toList().toArray(new String[0]);
		Configuration configuration = new ConfigurationBuilder()
				.setParallel(true)
				.forPackages(packageNames)
				.setScanners(Scanners.SubTypes.filterResultsBy(pred->true), Scanners.Resources);
		Reflections reflections = new Reflections(configuration);
		return reflections.getSubTypesOf(Object.class).toArray(new Class[0]);
	}

	@Override
	public Class[] getEventClasses() {
		return new Class[] {EventJS.class};
	}

	/**
	 * Get the path to the working directory of the current platform.
	 */
	@Override
	public Path getWorkingDirectory() {
		return FabricLoader.getInstance().getGameDir();
	}
}
