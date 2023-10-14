package pie.ilikepiefoo.kubejsoffline.data.populate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.data.AnnotationData;
import pie.ilikepiefoo.kubejsoffline.data.ArrayTypeData;
import pie.ilikepiefoo.kubejsoffline.data.ClassData;
import pie.ilikepiefoo.kubejsoffline.data.ConstructorData;
import pie.ilikepiefoo.kubejsoffline.data.FieldData;
import pie.ilikepiefoo.kubejsoffline.data.MethodData;
import pie.ilikepiefoo.kubejsoffline.data.ParameterData;
import pie.ilikepiefoo.kubejsoffline.data.ParametrizedData;
import pie.ilikepiefoo.kubejsoffline.data.TypeData;
import pie.ilikepiefoo.kubejsoffline.data.TypeVariableData;
import pie.ilikepiefoo.kubejsoffline.data.WildcardData;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Comparator;

public class DataPopulate {
    private static final Logger LOG = LogManager.getLogger();
    private static DataPopulate instance;

    public DataPopulate() {
    }

    public static DataPopulate getInstance() {
        if (instance == null) {
            instance = new DataPopulate();
        }
        return instance;
    }

    /**
     * The purpose of this method is to wrap a reference to a class, generic type, array type,
     * or wildcard type into a TypeData object.
     * All ClassData objects will only wrap their superclasses and implemented interfaces.
     * All methods, fields, constructors, and annotations should be handled by the populate method.
     *
     * @param subject The type to wrap.
     * @return The wrapped type.
     */
    public synchronized TypeData wrap(Type subject) {
        if (subject == null) {
            return null;
        }
        if (DataMapper.getInstance().contains(subject)) {
            return DataMapper.getInstance().get(subject);
        }

        if (subject instanceof WildcardType wildcardType) {
            return getWildcard(wildcardType);
        }

        if (subject instanceof TypeVariable<?> parameterizedType) {
            return getTypeVariable(parameterizedType);
        }

        if (subject instanceof ParameterizedType parameterizedType) {
            return getParameterizedType(parameterizedType);
        }

        String uniqueName = SafeOperations.safeUniqueTypeName(subject, true);

        int depth = 0;
        Type componentType = subject;
        while (componentType instanceof GenericArrayType) {
            componentType = ((GenericArrayType) componentType).getGenericComponentType();
            depth++;
        }

        if (!(componentType instanceof Class<?> clazz)) {
            if (depth > 0) {
                return DataMapper.getInstance().save(subject, new ArrayTypeData(uniqueName, wrap(componentType), depth));
            }
            LOG.warn("Component type '{}' is not a class!", componentType);
            return null;
        }
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
            depth++;
        }

        if (depth > 0) {
            return DataMapper.getInstance().save(subject, new ArrayTypeData(uniqueName, wrap(clazz), depth));
        }

        return getClassData(clazz);
    }

    @Nonnull
    private TypeData getWildcard(WildcardType wildcardType) {
        var upper = SafeOperations.tryGet(wildcardType::getUpperBounds).filter((bounds) -> bounds.length > 1).map((bounds) -> bounds[0]);
        var lower = SafeOperations.tryGet(wildcardType::getLowerBounds).filter((bounds) -> bounds.length > 1).map((bounds) -> bounds[0]);
        if (lower.isEmpty() && upper.isEmpty()) {
            return new WildcardData("?", wrap(Object.class));
        }
        var data = new WildcardData(SafeOperations.safeUniqueTypeName(wildcardType), wrap(lower.or(() -> upper).get()));
        return DataMapper.getInstance().save(wildcardType, data);
    }

    @Nonnull
    private TypeData getTypeVariable(TypeVariable<?> parameterizedType) {
        if (DataMapper.getInstance().contains(parameterizedType)) {
            return DataMapper.getInstance().get(parameterizedType);
        }

        String name = SafeOperations.safeUniqueTypeName(parameterizedType, true);
        var type = new TypeVariableData(name, parameterizedType.getName());
        DataMapper.getInstance().save(parameterizedType, type);

        var result = SafeOperations.tryGet(parameterizedType::getBounds).map(Arrays::stream).map((t) -> t.map(this::wrap)).map(
                (t) -> t.toArray(TypeData[]::new));

        result.ifPresent(type::addBounds);

        return type;
    }

    @Nonnull
    private TypeData getParameterizedType(ParameterizedType parameterizedType) {
        if (DataMapper.getInstance().contains(parameterizedType)) {
            return DataMapper.getInstance().get(parameterizedType);
        }
        ClassData rootClass = wrapToClassData(parameterizedType.getRawType());

        String name = SafeOperations.safeUniqueTypeName(parameterizedType, true);

        var data = new ParametrizedData(name, parameterizedType, rootClass);
        DataMapper.getInstance().save(parameterizedType, data);

        var inner_type_arguments = SafeOperations.tryGet(parameterizedType::getOwnerType)
                .filter((ownerType) -> ownerType instanceof ParameterizedType)
                .map((ownerType) -> ((ParameterizedType) ownerType).getActualTypeArguments())
                .map(Arrays::stream)
                .map((t) -> t.map(this::wrap))
                .map((t) -> t.toArray(TypeData[]::new))
                .orElse(new TypeData[0]);

        var outer_type_arguments = SafeOperations.tryGet(parameterizedType::getActualTypeArguments)
                .map(Arrays::stream)
                .map((t) -> t.map(this::wrap))
                .map((t) -> t.toArray(TypeData[]::new))
                .orElse(new TypeData[0]);

        // Add Type Arguments
        data.addTypeArguments(
                inner_type_arguments
        );
        data.addTypeArguments(
                outer_type_arguments
        );

        return data;
    }

    private ClassData getClassData(Class<?> clazz) {
        if (DataMapper.getInstance().containsClassData(clazz)) {
            return DataMapper.getInstance().getClassData(clazz);
        }
        String name = SafeOperations.safeUniqueTypeName(clazz, true);
        var type = new ClassData(name, clazz.getModifiers(), clazz);
        DataMapper.getInstance().save(clazz, type);

        if (clazz.isMemberClass()) {
            type.setOuterClass((ClassData) wrap(clazz.getDeclaringClass()));
        }

        SafeOperations.tryGetFirst(clazz::getGenericSuperclass, clazz::getSuperclass).map(this::wrapToClassData).ifPresent(type::addSuperClasses);

        SafeOperations.tryGet(clazz::getGenericInterfaces).map(Arrays::stream).map((t) -> t.map(this::wrapToClassData)).map(
                (t) -> t.toArray(ClassData[]::new)).ifPresent(type::addImplementedInterfaces);

        SafeOperations.tryGet(clazz::getInterfaces).map(Arrays::stream).map((t) -> t.map(this::wrapToClassData)).map(
                (t) -> t.toArray(ClassData[]::new)).ifPresent(type::addImplementedInterfaces);

        return type;
    }

    private ClassData wrapToClassData(Type type) {
        var result = wrap(type);
        if (result instanceof ClassData) {
            return (ClassData) result;
        }
        if (result instanceof ParametrizedData data) {
            return data.getRawType();
        }
        LOG.warn("Unable to wrap '{}' to ClassData!", type);
        throw new NullPointerException("Unable to wrap '" + type + "' to ClassData!");
    }

    public void clear() {
        DataMapper.getInstance().clear();
    }

    public void populateTree(ClassData data) {
        if (data == null) {
            return;
        }
        if (data.populated.get()) {
            return;
        }
        this.populate(data);
        data.getSuperClasses()
                .parallelStream()
                .filter((t) -> t instanceof ClassData)
                .map((t) -> (ClassData) t)
                .forEach(this::populateTree);
        data.getImplementedInterfaces()
                .parallelStream()
                .filter((t) -> t instanceof ClassData)
                .map((t) -> (ClassData) t)
                .forEach(this::populateTree);
        this.populateTree(data.getOuterClass());
    }

    private ParameterData[] getParameters(Parameter[] parameters) {
        if (parameters == null || parameters.length == 0) {
            return new ParameterData[0];
        }
        ParameterData[] array = new ParameterData[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Type type = SafeOperations.tryGetFirst(
                    parameters[i]::getParameterizedType,
                    parameters[i]::getType
            ).orElseThrow(() -> new NullPointerException("Type cannot be null"));
            array[i] = new ParameterData(
                    parameters[i].getModifiers(),
                    SafeOperations.safeUnwrapName(parameters[i]),
                    wrap(type)
            );
            array[i].addAnnotations(this.getAnnotations(parameters[i]));
        }
        return array;
    }

    private ConstructorData[] getConstructors(Constructor<?>[] data) {
        if (data == null || data.length == 0) {
            return new ConstructorData[0];
        }
        ConstructorData[] array = new ConstructorData[data.length];
        for (int i = 0; i < data.length; i++) {
            array[i] = new ConstructorData(data[i].getModifiers());
            array[i].addParameters(this.getParameters(data[i].getParameters()));
            array[i].addAnnotations(this.getAnnotations(data[i]));
        }
        return array;
    }

    private MethodData[] getMethods(Method[] declaredMethods) {
        if (declaredMethods == null || declaredMethods.length == 0) {
            return new MethodData[0];
        }
        MethodData[] array = new MethodData[declaredMethods.length];
        for (int i = 0; i < declaredMethods.length; i++) {
            array[i] = new MethodData(declaredMethods[i].getModifiers(), SafeOperations.safeUnwrapName(declaredMethods[i]),
                    wrap(declaredMethods[i].getGenericReturnType()));
            array[i].addParameters(this.getParameters(declaredMethods[i].getParameters()));
            array[i].addAnnotations(this.getAnnotations(declaredMethods[i]));
        }
        return array;
    }

    private FieldData[] getFields(Field[] declaredFields) {
        if (declaredFields == null || declaredFields.length == 0) {
            return new FieldData[0];
        }
        FieldData[] array = new FieldData[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {
            array[i] = new FieldData(declaredFields[i].getModifiers(), SafeOperations.safeUnwrapName(declaredFields[i]),
                    wrap(declaredFields[i].getGenericType()));
            array[i].addAnnotations(this.getAnnotations(declaredFields[i]));
        }
        return array;
    }

    public JsonObject toJson() {
        JsonObject result = new JsonObject();
        LOG.info("Populating {} classes", DataMapper.getInstance().getClassData().size());
        ClassData[] classData = DataMapper.getInstance().getClassData()
                .parallelStream()
                .map(this::populate)
                .sorted(Comparator.comparingInt(ClassData::getId))
                .toArray(ClassData[]::new);
        JsonArray array = new JsonArray(classData.length);
        for (ClassData data : classData) {
            array.add(data.toJSON());
        }
        result.add("classes", array);

        for (DataMapper.TypeDataIdentifier identifier : DataMapper.TypeDataIdentifier.values()) {
            if (identifier == DataMapper.TypeDataIdentifier.ALL || identifier == DataMapper.TypeDataIdentifier.CLASS) {
                continue;
            }
            var map = DataMapper.getInstance().getMap(identifier);
            LOG.info("Storing {} {} types", map.size(), identifier.name().toLowerCase());
            array = new JsonArray(map.size());
            var sortedArray =
                    map.values()
                            .parallelStream()
                            .sorted(Comparator.comparingInt((t) -> DataMapper.getInstance().getIdentifier(t)))
                            .map(
                                    TypeData::toJSON
                            )
                            .toArray(JsonElement[]::new);
            for (var data : sortedArray) {
                array.add(data);
            }
            result.add(identifier.name().toLowerCase(), array);
        }

        return result;
    }

    public AnnotationData[] getAnnotations(AnnotatedElement element) {
        if (element == null) {
            return new AnnotationData[0];
        }
        var data = element.getAnnotations();
        if (data == null || data.length == 0) {
            return new AnnotationData[0];
        }

        AnnotationData[] array = new AnnotationData[data.length];
        for (int i = 0; i < data.length; i++) {
            array[i] = new AnnotationData(wrapToClassData(data[i].annotationType()), data[i].toString());
        }
        return array;
    }

    /**
     * The goal of this method is to populate the data of a class.
     * This includes fields, methods, constructors, and annotations.
     *
     * @param data The class data to populate.
     * @return The populated class data.
     */
    public ClassData populate(ClassData data) {
        if (data == null) {
            return null;
        }
        if (data.populated.getAndSet(true)) {
            return data;
        }
        var clazz = data.getSourceClass();

        // Add Annotations
        data.setAnnotations(this.getAnnotations(clazz));

        // Add Fields
        data.setFields(this.getFields(clazz.getDeclaredFields()));

        // Add Methods
        data.setMethods(this.getMethods(clazz.getDeclaredMethods()));

        // Add Constructors
        data.setConstructors(this.getConstructors(clazz.getDeclaredConstructors()));

        // Add Type Variables
        data.setGenericParameters(Arrays.stream(clazz.getTypeParameters()).map(this::wrap).toArray(TypeData[]::new));

        return data;
    }

}
