package pie.ilikepiefoo.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * This class allows for each platform to have a different set of event classes
 * and a different set of starting classes for searching.
 */
public interface ReflectionHelper {
	Logger LOG = LogManager.getLogger();

	/**
	 * Retrieve all classes currently loaded by the JVM.
	 * @return All classes currently loaded by the JVM.
	 */
	Class[] getClasses();

	/**
	 * Retrieve all classes that should be displayed on the documentation page as events.
	 * @return All parents of classes that should be displayed on the documentation page as events.
	 */
	Class[] getEventClasses();

	/**
	 * Get the path to the working directory of the current platform.
	 */
	Path getWorkingDirectory();
}
