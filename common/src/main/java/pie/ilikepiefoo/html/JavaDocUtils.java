package pie.ilikepiefoo.html;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JavaDocUtils {
	public static final Logger LOG = LogManager.getLogger();
	public static ArrayList<String> TEST = new ArrayList<>();

	public static void getParameters(Type type) {
		if(type == null)
			return;
		LOG.info("Parameters for {}:", type.getTypeName());
		if(type instanceof ParameterizedType parameterizedType) {
			LOG.info("Owner Type: {}:", parameterizedType.getOwnerType());
			LOG.info("Raw Type: {}:", parameterizedType.getRawType());
			for(var possibleType : parameterizedType.getActualTypeArguments()) {
				LOG.info(possibleType);
				if(possibleType instanceof Class<?> target) {
					LOG.info("Possible Type is actually class {}", target);
				}
			}

		}
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("Test");
		try {
			getParameters(JavaDocUtils.class.getField("TEST").getGenericType());
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
}
