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
		SafeOperations.tryGet(subject::getComponentType).ifPresent((componentType) -> addToQueue(componentType, RelationType.COMPONENT_OF, subject));

		// Search Declared and Inner Classes
		SafeOperations.tryGet(subject::getClasses).ifPresent((classes)-> safeAddArray(classes, RelationType.INNER_TYPE_OF, subject));

		// Search Interfaces
		SafeOperations.tryGet(subject::getInterfaces).ifPresent((interfaces)-> safeAddArray(interfaces, RelationType.IMPLEMENTATION_OF, subject));

		// Search Generic Interfaces
		SafeOperations.tryGet(subject::getGenericInterfaces).ifPresent((interfaces)-> safeAddAllGeneric(interfaces, RelationType.GENERIC_IMPLEMENTATION_OF, subject));

		// Search Nest Members
		SafeOperations.tryGet(subject::getNestMembers).ifPresent((nestMembers)-> safeAddArray(nestMembers, RelationType.NEST_MEMBER_OF, subject));

		// Add Nest Host
		SafeOperations.tryGet(subject::getNestHost).ifPresent((nestHost)-> addToQueue(nestHost, RelationType.NEST_HOST_OF, subject));

		// Add Super Class
		SafeOperations.tryGet(subject::getSuperclass).ifPresent((superClass)-> addToQueue(superClass, RelationType.SUPER_CLASS_OF, subject));

		// Add Generic Super Class
		SafeOperations.tryGet(subject::getGenericSuperclass).ifPresent((superClass)-> safeAddGeneric(superClass, RelationType.GENERIC_SUPER_CLASS_OF, subject));

		// Search Permitted Subclasses
		SafeOperations.tryGet(subject::getPermittedSubclasses).ifPresent((permittedSubclasses)-> safeAddArray(permittedSubclasses, RelationType.PERMITTED_SUBCLASS_OF, subject));

		// Search Annotations
		SafeOperations.tryGet(subject::getAnnotations).ifPresent((annotations)-> safeAddAllAnnotation(annotations, RelationType.ANNOTATION_OF, subject));

		// Add All Generic Type Parameters
		SafeOperations.tryGet(subject::getTypeParameters).ifPresent((typeParameters) -> {
			for(var type : typeParameters){
				SafeOperations.tryGet(type::getGenericDeclaration).ifPresent((genericDeclaration) -> addToQueue(genericDeclaration, RelationType.GENERIC_CLASS_PARAMETER_OF, subject));
			}
		});

		// Add Fields
		SafeOperations.tryGet(subject::getDeclaredFields).ifPresent((fields) -> {
			for(var field : fields) {
				SafeOperations.tryGet(field::getType).ifPresent((type) -> addToQueue(type, RelationType.DECLARED_FIELD_TYPE_OF, subject));
				SafeOperations.tryGet(field::getGenericType).ifPresent((type) -> safeAddGeneric(type, RelationType.DECLARED_GENERIC_FIELD_TYPE_OF, subject));
				SafeOperations.tryGet(field::getAnnotations).ifPresent((annotations) -> safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
			}
		});

		// Add Declared Methods
		SafeOperations.tryGet(subject::getDeclaredMethods).ifPresent((methods) -> {
			for (var method : methods) {
				SafeOperations.tryGet(method::getReturnType).ifPresent((type) -> addToQueue(type, RelationType.DECLARED_METHOD_RETURN_TYPE_OF, subject));
				SafeOperations.tryGet(method::getParameterTypes).ifPresent((types) -> safeAddArray(types, RelationType.DECLARED_METHOD_PARAMETER_TYPE_OF, subject));
				SafeOperations.tryGet(method::getExceptionTypes).ifPresent((types) -> safeAddArray(types, RelationType.DECLARED_METHOD_EXCEPTION_TYPE_OF, subject));
				SafeOperations.tryGet(method::getGenericReturnType).ifPresent((type) -> safeAddGeneric(type, RelationType.DECLARED_GENERIC_METHOD_RETURN_TYPE_OF, subject));
				SafeOperations.tryGet(method::getGenericParameterTypes).ifPresent((types) -> safeAddAllGeneric(types, RelationType.DECLARED_GENERIC_METHOD_PARAMETER_TYPE_OF, subject));
				SafeOperations.tryGet(method::getGenericExceptionTypes).ifPresent((types) -> safeAddAllGeneric(types, RelationType.DECLARED_GENERIC_METHOD_EXCEPTION_TYPE_OF, subject));
				SafeOperations.tryGet(method::getAnnotations).ifPresent((annotations) -> safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
				SafeOperations.tryGet(method::getParameterAnnotations).ifPresent((annotations) -> safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
			}
		});

		// Add Constructors
		SafeOperations.tryGet(subject::getDeclaredConstructors).ifPresent((constructors) -> {
			for (var constructor : constructors) {
				SafeOperations.tryGet(constructor::getParameterTypes).ifPresent((types) -> safeAddArray(types, RelationType.CONSTRUCTOR_PARAMETER_TYPE_OF, subject));
				SafeOperations.tryGet(constructor::getExceptionTypes).ifPresent((types) -> safeAddArray(types, RelationType.CONSTRUCTOR_EXCEPTION_TYPE_OF, subject));
				SafeOperations.tryGet(constructor::getGenericParameterTypes).ifPresent((types) -> safeAddAllGeneric(types, RelationType.CONSTRUCTOR_GENERIC_PARAMETER_TYPE_OF, subject));
				SafeOperations.tryGet(constructor::getGenericExceptionTypes).ifPresent((types) -> safeAddAllGeneric(types, RelationType.CONSTRUCTOR_EXCEPTION_TYPE_OF, subject));
				SafeOperations.tryGet(constructor::getAnnotations).ifPresent((annotations) -> safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
				SafeOperations.tryGet(constructor::getParameterAnnotations).ifPresent((annotations) -> safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
			}
		});

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
		for (Annotation[] parameterAnnotation : parameterAnnotations) {
			safeAddAllAnnotation(parameterAnnotation, relationType, subject);
		}
	}

	private void safeAddAllAnnotation(Annotation[] annotations, RelationType relationType, Class<?> subject) {
		if(annotations == null)
			return;
		for(var annotation : annotations) {
			SafeOperations.tryGet(annotation::annotationType).ifPresent((type) -> addToQueue(type, relationType, subject));
		}
	}

	private void addToQueue(Class<?> target, Class<?> subject) {
		addToQueue(target, RelationType.UNKNOWN, subject);
	}

	private void addToQueue(Class<?> target, RelationType relationType, Class<?> subject) {
		if(target == null)
			return;
		if(!CLASS_SEARCH.containsKey(target)) {
			synchronized (this) {
				NEXT_DEPTH.add(target);
				CLASS_SEARCH.put(target, SearchState.IN_QUEUE);
			}
		}
		if(target != subject && RelationType.UNKNOWN != relationType) {
			RELATIONSHIPS.add(new Relation(target, relationType, subject));
		}
	}

	private void safeAddGeneric(Type type, Class<?> subject) {
		safeAddGeneric(type, RelationType.UNKNOWN, subject);
	}

	private void safeAddGeneric(Type type, RelationType relationType, Class<?> subject) {
		if(type == null)
			return;
		if(type instanceof ParameterizedType parameterizedType) {
			SafeOperations.tryGet(parameterizedType::getRawType).ifPresent((rawType) -> addToQueue((Class<?>) rawType, RelationType.UNKNOWN, subject));
			SafeOperations.tryGet(parameterizedType::getActualTypeArguments).ifPresent((types) ->  {
				for(var possibleType : types) {
					safeAddGeneric(possibleType, relationType, subject);
				}
			});
		}
		if(type instanceof Class<?> clazz) {
			addToQueue(clazz, relationType, subject);
		}
	}

	private void safeAddAllGeneric(Type[] targets, Class<?> subject){
		safeAddAllGeneric(targets, RelationType.UNKNOWN, subject);
	}
	private void safeAddAllGeneric(Type[] targets, RelationType relationType, Class<?> subject){
		if(targets == null)
			return;
		for(var target : targets) {
			safeAddGeneric(target, relationType, subject);
		}
	}

	private void safeAddArray(Class<?>[] targets, Class<?> subject){
		safeAddArray(targets, RelationType.UNKNOWN, subject);
	}

	private void safeAddArray(Class<?>[] targets, RelationType relationType, Class<?> subject){
		if(targets == null)
			return;
		for(var target : targets) {
			addToQueue(target, relationType, subject);
		}
	}

	public enum SearchState {
		IN_QUEUE,
		SEARCHED,
		BEING_SEARCHED
	}
}
