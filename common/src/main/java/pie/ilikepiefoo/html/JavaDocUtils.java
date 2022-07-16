package pie.ilikepiefoo.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.DocumentationThread;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JavaDocUtils {
	public static final Logger LOG = LogManager.getLogger();


	public static void main(String[] args) {
		new DocumentationThread().run();
	}
}
