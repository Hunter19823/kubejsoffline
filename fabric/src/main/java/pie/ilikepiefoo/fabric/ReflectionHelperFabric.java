package pie.ilikepiefoo.fabric;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import pie.ilikepiefoo.util.ReflectionHelper;

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
		Class[] classes = reflections.getSubTypesOf(Object.class).toArray(new Class[0]);
		return classes;
	}
}
