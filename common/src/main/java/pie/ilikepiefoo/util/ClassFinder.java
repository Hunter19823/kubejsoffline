package pie.ilikepiefoo.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

public class ClassFinder {
	public static final ClassFinder INSTANCE = new ClassFinder();
	public static final Logger LOG = LogManager.getLogger();
	public static boolean DEBUG = false;
	public final Map<Class<?>, SearchState> CLASS_SEARCH;
	private Queue<Class<?>> CURRENT_DEPTH;
	private Queue<Class<?>> NEXT_DEPTH;

	private ClassFinder() {
		CLASS_SEARCH = new ConcurrentHashMap<>();
		CURRENT_DEPTH = new ConcurrentLinkedQueue<>();
		NEXT_DEPTH = new ConcurrentLinkedQueue<>();
	}

	public boolean isFinished() {
		return CURRENT_DEPTH.isEmpty() && NEXT_DEPTH.isEmpty();
	}

	public void addToSearch(Class<?>... target) {
		if(target == null)
			return;
		NEXT_DEPTH.addAll(Arrays.asList(target));
	}

	public void searchCurrentDepth(){
		var temp = CURRENT_DEPTH;
		CURRENT_DEPTH = NEXT_DEPTH;
		NEXT_DEPTH = temp;
		if(CURRENT_DEPTH.isEmpty()){
			LOG.info("Search Stack is Empty.");
		}

		CURRENT_DEPTH.parallelStream().forEach((subject) -> {
			if(DEBUG)
				LOG.info("Now Searching {}", subject);
			search(subject);
		});
		CURRENT_DEPTH.clear();
	}


	public void search(Class<?> subject) {
		CLASS_SEARCH.put(subject, SearchState.BEING_SEARCHED);

		// Search ComponentType
		addToQueue(subject.getComponentType());

		// Search Interfaces and Inner Classes
		safeAddArray(subject.getClasses());

		// Search Nest Members
		safeAddArray(subject.getNestMembers());

		// Add Nest Host
		try{
			addToQueue(subject.getNestHost());
		} catch (Throwable e) {
			LOG.error(e);
		}

		// Add Super Class
		try {
			addToQueue(subject.getSuperclass());
		} catch (Throwable e) {
			LOG.error(e);
		}

		// Search Explicit Implementations
		safeAddArray(subject.getPermittedSubclasses());

		// Add All Generic Type Parameters
		try{
			for(var type : subject.getTypeParameters())
				addToQueue(type.getGenericDeclaration());
		} catch (Throwable e) {
			LOG.error(e);
		}

		// Add Fields
		try{
			for(var field : subject.getDeclaredFields())
				addToQueue(field.getType());
		} catch (Throwable e) {
			LOG.error(e);
		}

		// Add Declared Methods
		try{
			for(var method : subject.getDeclaredMethods()) {
				addToQueue(method.getReturnType());
				safeAddArray(method.getParameterTypes());
				safeAddArray(method.getExceptionTypes());
			}
		} catch (Throwable e) {
			LOG.error(e);
		}

		// Add Constructors
		try{
			for(var constructor : subject.getConstructors()) {
				addToQueue(constructor.getDeclaringClass());
				safeAddArray(constructor.getParameterTypes());
				safeAddArray(constructor.getExceptionTypes());
			}
		} catch (Throwable e) {
			LOG.error(e);
		}

		CLASS_SEARCH.put(subject, SearchState.SEARCHED);
	}

	private void addToQueue(Class<?> target) {
		if(target == null)
			return;
		if(!CLASS_SEARCH.containsKey(target)) {
			NEXT_DEPTH.add(target);
			CLASS_SEARCH.put(target, SearchState.IN_QUEUE);
		}
	}

	private void safeAddArray(Class<?>[] targets){
		if(targets == null)
			return;
		for(var target : targets) {
			try {
				addToQueue(target);
			} catch (Throwable e){
				LOG.error(e);
			}
		}
	}

	public enum SearchState {
		IN_QUEUE,
		SEARCHED,
		BEING_SEARCHED
	}
}
