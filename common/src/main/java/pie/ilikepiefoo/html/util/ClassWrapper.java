package pie.ilikepiefoo.html.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClassWrapper<T> {
	public static final Logger LOG = LogManager.getLogger();

	private final Class<T> subject;

	public ClassWrapper(Class<T> subject) {
		this.subject = subject;
	}

	public Class<T> getSubject() {
		return subject;
	}

	public static <T> ClassWrapper<T> of(Class<T> type) {
		return new ClassWrapper<T>(type);
	}

	public Class<?> getTrueType() {
		Class<?> non_array = subject;
		while(non_array.getComponentType() != null) {
			non_array = non_array.getComponentType();
		}
		return non_array;
	}

	public void print() {
		LOG.info("~~~~~~~~~~~~~~~");
		LOG.info("getSimpleName: {}", subject.getSimpleName());
		LOG.info("getName: {}", subject.getName());
		LOG.info("getCanonicalName: {}", subject.getCanonicalName());
		LOG.info("getTypeName: {}", subject.getTypeName());
		LOG.info("getSimpleName: {}", subject.getSimpleName());
		LOG.info("getPackageName: {}", subject.getPackageName());
		LOG.info("getComponentType: {}", subject.getComponentType());
		LOG.info("getNestHost: {}", subject.getNestHost());
		LOG.info("toGenericString: {}", subject.toGenericString());
		LOG.info("componentType: {}", subject.componentType());
		LOG.info("arrayType: {}", subject.arrayType());
		for (int i = 0; i < subject.getTypeParameters().length; i++) {
			LOG.info("getTypeParameters #{}: {}", i,
					subject.getTypeParameters()[i]);
		}
	}
}
