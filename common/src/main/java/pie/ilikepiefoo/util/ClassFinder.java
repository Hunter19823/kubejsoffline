package pie.ilikepiefoo.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class ClassFinder {
	public static final ClassFinder INSTANCE = new ClassFinder();
	public static final Logger LOG = LogManager.getLogger();
	public static boolean DEBUG = false;
	public final Map<Class<?>, SearchState> CLASS_SEARCH;
	public final Set<Relation> RELATIONSHIPS;
	private Queue<Class<?>> CURRENT_DEPTH;
	private Queue<Class<?>> NEXT_DEPTH;
	private final Queue<Consumer<Class<?>>> HANDLERS;

	private ClassFinder() {
		CLASS_SEARCH = new ConcurrentHashMap<>();
		CURRENT_DEPTH = new ConcurrentLinkedQueue<>();
		NEXT_DEPTH = new ConcurrentLinkedQueue<>();
		HANDLERS = new ConcurrentLinkedQueue<>();
		RELATIONSHIPS = ConcurrentHashMap.newKeySet();
	}

	public void clear(){
		CLASS_SEARCH.clear();
		CURRENT_DEPTH.clear();
		NEXT_DEPTH.clear();
		HANDLERS.clear();
		RELATIONSHIPS.clear();
	}

	public void onSearched(Consumer<Class<?>> handler) {
		HANDLERS.add(handler);
	}

	public Set<Relation> getRelationships() {
		return RELATIONSHIPS;
	}

	public boolean isFinished() {
		return CURRENT_DEPTH.isEmpty() && NEXT_DEPTH.isEmpty();
	}

	public void addToSearch(Class<?>... target) {
		if(target == null)
			return;
		for (Class<?> aClass : target) {
			try {
				NEXT_DEPTH.add(aClass);
			} catch (Throwable e) {
				if(DEBUG)
					LOG.error(e);
			}
		}
	}

	public void searchCurrentDepth(){
		var temp = CURRENT_DEPTH;
		CURRENT_DEPTH = NEXT_DEPTH;
		NEXT_DEPTH = temp;
		if(CURRENT_DEPTH.isEmpty()){
			LOG.info("Search Stack is Empty.");
		}

		CURRENT_DEPTH.stream().forEach((subject) -> {
			if(DEBUG)
				LOG.info("Now Searching {}", subject);
			search(subject);
		});
		CURRENT_DEPTH.clear();
	}


	public void search(Class<?> subject) {
		CLASS_SEARCH.put(subject, SearchState.BEING_SEARCHED);

		// Search ComponentType
		try{
			addToQueue(subject.getComponentType(), RelationType.COMPONENT_OF, subject);
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Search Interfaces and Inner Classes
		try{
			safeAddArray(subject.getClasses(), RelationType.INNER_TYPE_OF, subject);
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Search Nest Members
		try{
			safeAddArray(subject.getNestMembers(), subject);
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Add Nest Host
		try{
			addToQueue(subject.getNestHost(), subject);
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Add Super Class
		try {
			addToQueue(subject.getSuperclass(), RelationType.SUPER_CLASS_OF, subject);
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Search Explicit Implementations
		safeAddArray(subject.getPermittedSubclasses(), RelationType.PERMITTED_SUBCLASS_OF, subject);

		// Search Annotations
		safeAddAllAnnotation(subject.getAnnotations(), RelationType.UNKNOWN, subject);

		// Add All Generic Type Parameters
		try{
			for(var type : subject.getTypeParameters())
				addToQueue(type.getGenericDeclaration(), RelationType.GENERIC_CLASS_PARAMETER_OF, subject);
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Add Fields
		try{
			for(var field : subject.getDeclaredFields()) {
				addToQueue(field.getType(), RelationType.DECLARED_FIELD_TYPE_OF, subject);
				addAllGenericTypes(field.getGenericType(), RelationType.DECLARED_GENERIC_FIELD_TYPE_OF, subject);
				safeAddAllAnnotation(field.getAnnotations(), RelationType.UNKNOWN, subject);
			}
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Add Declared Methods
		try{
			for(var method : subject.getDeclaredMethods()) {
				addToQueue(method.getReturnType(), RelationType.DECLARED_METHOD_RETURN_TYPE_OF, subject);
				safeAddArray(method.getParameterTypes(), RelationType.DECLARED_METHOD_PARAMETER_TYPE_OF, subject);
				safeAddArray(method.getExceptionTypes(), RelationType.DECLARED_METHOD_EXCEPTION_TYPE_OF, subject);
				addAllGenericTypes(method.getGenericReturnType(), RelationType.DECLARED_GENERIC_METHOD_RETURN_TYPE_OF, subject);
				safeAddAllGeneric(method.getGenericParameterTypes(), RelationType.DECLARED_GENERIC_METHOD_PARAMETER_TYPE_OF, subject);
				safeAddAllGeneric(method.getGenericExceptionTypes(), RelationType.DECLARED_GENERIC_METHOD_EXCEPTION_TYPE_OF, subject);
				safeAddAllAnnotation(method.getAnnotations(), RelationType.UNKNOWN, subject);
				safeAddAllAnnotation(method.getParameterAnnotations(), RelationType.UNKNOWN, subject);
			}
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		// Add Constructors
		try{
			for(var constructor : subject.getConstructors()) {
				addToQueue(constructor.getDeclaringClass(), subject);
				safeAddArray(constructor.getParameterTypes(), RelationType.CONSTRUCTOR_PARAMETER_TYPE_OF, subject);
				safeAddArray(constructor.getExceptionTypes(), RelationType.CONSTRUCTOR_EXCEPTION_TYPE_OF, subject);
				safeAddAllGeneric(constructor.getGenericParameterTypes(), RelationType.CONSTRUCTOR_GENERIC_PARAMETER_TYPE_OF, subject);
				safeAddAllGeneric(constructor.getGenericExceptionTypes(), RelationType.CONSTRUCTOR_GENERIC_EXCEPTION_TYPE_OF, subject);
				safeAddAllAnnotation(constructor.getAnnotations(), RelationType.UNKNOWN, subject);
				safeAddAllAnnotation(constructor.getParameterAnnotations(), RelationType.UNKNOWN, subject);
			}
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}

		CLASS_SEARCH.put(subject, SearchState.SEARCHED);
		for(Consumer<Class<?>> handler : HANDLERS) {
			try {
				handler.accept(subject);
			} catch (Throwable e) {
				if(DEBUG)
					LOG.error(e);
			}
		}
	}

	private void safeAddAllAnnotation(Annotation[][] parameterAnnotations, RelationType relationType, Class<?> subject) {
		if(parameterAnnotations == null)
			return;
		try {
			for (Annotation[] parameterAnnotation : parameterAnnotations) {
				safeAddAllAnnotation(parameterAnnotation, relationType, subject);
			}
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}
	}

	private void safeAddAllAnnotation(Annotation[] annotations, RelationType relationType, Class<?> subject) {
		if(annotations == null)
			return;
		try {
			for(var annotation : annotations) {
				addToQueue(annotation.annotationType(), relationType, subject);
			}
		} catch (Throwable e) {
			if(DEBUG)
				LOG.error(e);
		}
	}

	private void addToQueue(Class<?> target, Class<?> subject) {
		addToQueue(target, RelationType.UNKNOWN, subject);
	}

	private void addToQueue(Class<?> target, RelationType relationType, Class<?> subject) {
		if(target == null)
			return;
		if(!CLASS_SEARCH.containsKey(target)) {
			NEXT_DEPTH.add(target);
			CLASS_SEARCH.put(target, SearchState.IN_QUEUE);
		}
		if(target != subject)
			RELATIONSHIPS.add(new Relation(target, relationType, subject));
	}

	private void addAllGenericTypes(Type type, Class<?> subject) {
		addAllGenericTypes(type, RelationType.UNKNOWN, subject);
	}

	private void addAllGenericTypes(Type type, RelationType relationType, Class<?> subject) {
		if(type == null)
			return;
		if(type instanceof ParameterizedType parameterizedType) {
			try {
				for (var possibleType : parameterizedType.getActualTypeArguments()) {
					if (possibleType instanceof Class<?> target) {
						addToQueue(target, relationType, subject);
					}
				}
			} catch (Throwable e) {
				if(DEBUG)
					LOG.error(e);
			}
		}
	}

	private void safeAddAllGeneric(Type[] targets, Class<?> subject){
		safeAddAllGeneric(targets, RelationType.UNKNOWN, subject);
	}
	private void safeAddAllGeneric(Type[] targets, RelationType relationType, Class<?> subject){
		if(targets == null)
			return;
		for(var target : targets) {
			try {
				addAllGenericTypes(target, relationType, subject);
			} catch (Throwable e){
				if(DEBUG)
					LOG.error(e);
			}
		}
	}

	private void safeAddArray(Class<?>[] targets, Class<?> subject){
		safeAddArray(targets, RelationType.UNKNOWN, subject);
	}

	private void safeAddArray(Class<?>[] targets, RelationType relationType, Class<?> subject){
		if(targets == null)
			return;
		for(var target : targets) {
			try {
				addToQueue(target, relationType, subject);
			} catch (Throwable e){
				if(DEBUG)
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
