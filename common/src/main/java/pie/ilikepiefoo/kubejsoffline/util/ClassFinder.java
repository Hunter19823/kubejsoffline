package pie.ilikepiefoo.kubejsoffline.util;

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
    public static boolean DEBUG;
    public final Map<Class<?>, SearchState> CLASS_SEARCH;
    public final Set<Relation> RELATIONSHIPS;
    private final Queue<Consumer<Class<?>>> HANDLERS;
    private Queue<Class<?>> CURRENT_DEPTH;
    private Queue<Class<?>> NEXT_DEPTH;

    private ClassFinder() {
        this.CLASS_SEARCH = new ConcurrentHashMap<>();
        this.CURRENT_DEPTH = new ConcurrentLinkedQueue<>();
        this.NEXT_DEPTH = new ConcurrentLinkedQueue<>();
        this.HANDLERS = new ConcurrentLinkedQueue<>();
        this.RELATIONSHIPS = ConcurrentHashMap.newKeySet();
    }

    public void clear() {
        this.CLASS_SEARCH.clear();
        this.CURRENT_DEPTH.clear();
        this.NEXT_DEPTH.clear();
        this.HANDLERS.clear();
        this.RELATIONSHIPS.clear();
    }

    public void onSearched(final Consumer<Class<?>> handler) {
        this.HANDLERS.add(handler);
    }

    public Set<Relation> getRelationships() {
        return this.RELATIONSHIPS;
    }

    public boolean isFinished() {
        return this.CURRENT_DEPTH.isEmpty() && this.NEXT_DEPTH.isEmpty();
    }

    public void addToSearch(final Class<?>... target) {
        if (null == target) {
            return;
        }
        for (final Class<?> aClass : target) {
            try {
                this.NEXT_DEPTH.add(aClass);
            } catch (final Throwable e) {
                if (DEBUG) {
                    LOG.error(e);
                }
            }
        }
    }

    public void searchCurrentDepth() {
        final var temp = this.CURRENT_DEPTH;
        this.CURRENT_DEPTH = this.NEXT_DEPTH;
        this.NEXT_DEPTH = temp;
        if (this.CURRENT_DEPTH.isEmpty()) {
            LOG.info("Search Stack is Empty.");
        }

        this.CURRENT_DEPTH.parallelStream().forEach((subject) -> {
            if (DEBUG) {
                LOG.info("Now Searching {}", subject);
            }
            this.search(subject);
        });
        this.CURRENT_DEPTH.clear();
    }


    public void search(final Class<?> subject) {
        this.CLASS_SEARCH.put(subject, SearchState.BEING_SEARCHED);

        // Search ComponentType
        SafeOperations.tryGet(subject::getComponentType).ifPresent((componentType) -> this.addToQueue(componentType, RelationType.COMPONENT_OF, subject));

        // Search Declared and Inner Classes
        SafeOperations.tryGet(subject::getClasses).ifPresent((classes) -> this.safeAddArray(classes, RelationType.INNER_TYPE_OF, subject));

        // Search Interfaces
        SafeOperations.tryGet(subject::getInterfaces).ifPresent((interfaces) -> this.safeAddArray(interfaces, RelationType.IMPLEMENTATION_OF, subject));

        // Search Generic Interfaces
        SafeOperations.tryGet(subject::getGenericInterfaces).ifPresent((interfaces) -> this.safeAddAllGeneric(interfaces, RelationType.GENERIC_IMPLEMENTATION_OF, subject));

        // Search Nest Members
        SafeOperations.tryGet(subject::getNestMembers).ifPresent((nestMembers) -> this.safeAddArray(nestMembers, RelationType.NEST_MEMBER_OF, subject));

        // Add Nest Host
        SafeOperations.tryGet(subject::getNestHost).ifPresent((nestHost) -> this.addToQueue(nestHost, RelationType.NEST_HOST_OF, subject));

        // Add Super Class
        SafeOperations.tryGet(subject::getSuperclass).ifPresent((superClass) -> this.addToQueue(superClass, RelationType.SUPER_CLASS_OF, subject));

        // Add Generic Super Class
        SafeOperations.tryGet(subject::getGenericSuperclass).ifPresent((superClass) -> this.safeAddGeneric(superClass, RelationType.GENERIC_SUPER_CLASS_OF, subject));

        // Search Permitted Subclasses
        SafeOperations.tryGet(subject::getPermittedSubclasses).ifPresent((permittedSubclasses) -> this.safeAddArray(permittedSubclasses, RelationType.PERMITTED_SUBCLASS_OF, subject));

        // Search Annotations
        SafeOperations.tryGet(subject::getAnnotations).ifPresent((annotations) -> this.safeAddAllAnnotation(annotations, RelationType.ANNOTATION_OF, subject));

        // Add All Generic Type Parameters
        SafeOperations.tryGet(subject::getTypeParameters).ifPresent((typeParameters) -> {
            for (final var type : typeParameters) {
                SafeOperations.tryGet(type::getGenericDeclaration).ifPresent((genericDeclaration) -> this.addToQueue(genericDeclaration, RelationType.GENERIC_CLASS_PARAMETER_OF, subject));
            }
        });

        // Add Fields
        SafeOperations.tryGet(subject::getDeclaredFields).ifPresent((fields) -> {
            for (final var field : fields) {
                SafeOperations.tryGet(field::getType).ifPresent((type) -> this.addToQueue(type, RelationType.DECLARED_FIELD_TYPE_OF, subject));
                SafeOperations.tryGet(field::getGenericType).ifPresent((type) -> this.safeAddGeneric(type, RelationType.DECLARED_GENERIC_FIELD_TYPE_OF, subject));
                SafeOperations.tryGet(field::getAnnotations).ifPresent((annotations) -> this.safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
            }
        });

        // Add Declared Methods
        SafeOperations.tryGet(subject::getDeclaredMethods).ifPresent((methods) -> {
            for (final var method : methods) {
                SafeOperations.tryGet(method::getReturnType).ifPresent((type) -> this.addToQueue(type, RelationType.DECLARED_METHOD_RETURN_TYPE_OF, subject));
                SafeOperations.tryGet(method::getParameterTypes).ifPresent((types) -> this.safeAddArray(types, RelationType.DECLARED_METHOD_PARAMETER_TYPE_OF, subject));
                SafeOperations.tryGet(method::getExceptionTypes).ifPresent((types) -> this.safeAddArray(types, RelationType.DECLARED_METHOD_EXCEPTION_TYPE_OF, subject));
                SafeOperations.tryGet(method::getGenericReturnType).ifPresent((type) -> this.safeAddGeneric(type, RelationType.DECLARED_GENERIC_METHOD_RETURN_TYPE_OF, subject));
                SafeOperations.tryGet(method::getGenericParameterTypes).ifPresent((types) -> this.safeAddAllGeneric(types, RelationType.DECLARED_GENERIC_METHOD_PARAMETER_TYPE_OF, subject));
                SafeOperations.tryGet(method::getGenericExceptionTypes).ifPresent((types) -> this.safeAddAllGeneric(types, RelationType.DECLARED_GENERIC_METHOD_EXCEPTION_TYPE_OF, subject));
                SafeOperations.tryGet(method::getAnnotations).ifPresent((annotations) -> this.safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
                SafeOperations.tryGet(method::getParameterAnnotations).ifPresent((annotations) -> this.safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
            }
        });

        // Add Constructors
        SafeOperations.tryGet(subject::getDeclaredConstructors).ifPresent((constructors) -> {
            for (final var constructor : constructors) {
                SafeOperations.tryGet(constructor::getParameterTypes).ifPresent((types) -> this.safeAddArray(types, RelationType.CONSTRUCTOR_PARAMETER_TYPE_OF, subject));
                SafeOperations.tryGet(constructor::getExceptionTypes).ifPresent((types) -> this.safeAddArray(types, RelationType.CONSTRUCTOR_EXCEPTION_TYPE_OF, subject));
                SafeOperations.tryGet(constructor::getGenericParameterTypes).ifPresent((types) -> this.safeAddAllGeneric(types, RelationType.CONSTRUCTOR_GENERIC_PARAMETER_TYPE_OF, subject));
                SafeOperations.tryGet(constructor::getGenericExceptionTypes).ifPresent((types) -> this.safeAddAllGeneric(types, RelationType.CONSTRUCTOR_EXCEPTION_TYPE_OF, subject));
                SafeOperations.tryGet(constructor::getAnnotations).ifPresent((annotations) -> this.safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
                SafeOperations.tryGet(constructor::getParameterAnnotations).ifPresent((annotations) -> this.safeAddAllAnnotation(annotations, RelationType.UNKNOWN, subject));
            }
        });

        this.CLASS_SEARCH.put(subject, SearchState.SEARCHED);
        for (final Consumer<Class<?>> handler : this.HANDLERS) {
            try {
                handler.accept(subject);
            } catch (final Throwable e) {
                if (DEBUG) {
                    LOG.error(e);
                }
            }
        }
    }

    private void safeAddAllAnnotation(final Annotation[][] parameterAnnotations, final RelationType relationType, final Class<?> subject) {
        if (null == parameterAnnotations) {
            return;
        }
        for (final Annotation[] parameterAnnotation : parameterAnnotations) {
            this.safeAddAllAnnotation(parameterAnnotation, relationType, subject);
        }
    }

    private void safeAddAllAnnotation(final Annotation[] annotations, final RelationType relationType, final Class<?> subject) {
        if (null == annotations) {
            return;
        }
        for (final var annotation : annotations) {
            SafeOperations.tryGet(annotation::annotationType).ifPresent((type) -> this.addToQueue(type, relationType, subject));
        }
    }

    private void addToQueue(final Class<?> target, final Class<?> subject) {
        this.addToQueue(target, RelationType.UNKNOWN, subject);
    }

    private void addToQueue(final Class<?> target, final RelationType relationType, final Class<?> subject) {
        if (null == target) {
            return;
        }
        synchronized (this.CLASS_SEARCH) {
            if (!this.CLASS_SEARCH.containsKey(target)) {
                this.NEXT_DEPTH.add(target);
                this.CLASS_SEARCH.put(target, SearchState.IN_QUEUE);
            }
            if (target != subject) {
                this.addRelation(new Relation(target, relationType, subject));
            }
        }
    }

    private void addRelation(final Relation relation) {
        if (null == relation) {
            return;
        }

        this.RELATIONSHIPS.add(relation);
    }

    private void safeAddGeneric(final Type type, final Class<?> subject) {
        this.safeAddGeneric(type, RelationType.UNKNOWN, subject);
    }

    private void safeAddGeneric(final Type type, final RelationType relationType, final Class<?> subject) {
        if (null == type) {
            return;
        }
        if (type instanceof ParameterizedType parameterizedType) {
            SafeOperations.tryGet(parameterizedType::getRawType).ifPresent((rawType) -> this.addToQueue((Class<?>) rawType, RelationType.UNKNOWN, subject));
            SafeOperations.tryGet(parameterizedType::getActualTypeArguments).ifPresent((types) -> this.safeAddAllGeneric(types, relationType, subject));
        }
        if (type instanceof Class<?> clazz) {
            this.addToQueue(clazz, relationType, subject);
        }
    }

    private void safeAddAllGeneric(final Type[] targets, final Class<?> subject) {
        this.safeAddAllGeneric(targets, RelationType.UNKNOWN, subject);
    }

    private void safeAddAllGeneric(final Type[] targets, final RelationType relationType, final Class<?> subject) {
        if (null == targets) {
            return;
        }
        for (final var target : targets) {
            this.safeAddGeneric(target, relationType, subject);
        }
    }

    private void safeAddArray(final Class<?>[] targets, final Class<?> subject) {
        this.safeAddArray(targets, RelationType.UNKNOWN, subject);
    }

    private void safeAddArray(final Class<?>[] targets, final RelationType relationType, final Class<?> subject) {
        if (null == targets) {
            return;
        }
        for (final var target : targets) {
            this.addToQueue(target, relationType, subject);
        }
    }

    public enum SearchState {
        IN_QUEUE,
        SEARCHED,
        BEING_SEARCHED
    }
}
