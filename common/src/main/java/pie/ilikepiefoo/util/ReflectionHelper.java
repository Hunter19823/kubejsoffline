package pie.ilikepiefoo.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface ReflectionHelper {
	public static final Logger LOG = LogManager.getLogger();
	Class[] getClasses();

	Class[] getEventClasses();
}
