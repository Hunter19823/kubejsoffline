package pie.ilikepiefoo.kubejsoffline.util;

import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SafeOperations {
    private static final Logger LOG = LogManager.getLogger();
    private static boolean loadedRemap;
    private static MinecraftRemapper remapper;

    private static Optional<MinecraftRemapper> getRemap() {
        if (!loadedRemap) {
            remapper = tryGet(RemappingHelper::getMinecraftRemapper).orElse(null);
            loadedRemap = true;
        }
        return Optional.ofNullable(remapper);
    }

    /**
     * Returns the full name of the object if it is a class,
     * An attempt will be made for the generic type before falling back to the raw type.
     * if it is a field, it returns the return type of the field,
     * if it is a method, it returns the return type of the method,
     * if it is a parameter, it returns the type of the parameter,
     * if it is a parameterized type, it returns the raw type of the parameterized type,
     * otherwise it returns the toString() of the object.
     *
     * @param obj Object to get the name of.
     * @return Name of the object.
     */
    public static String safeUnwrapReturnTypeName(final Object obj) {
        return safeUniqueTypeName(safeUnwrapReturnType(obj));
    }

    public static String safeUniqueTypeName(Type type) {
        if (null == type) {
            return null;
        }
        return getNestedTypeName(type, null);
    }

    public static Type safeUnwrapReturnType(final Object obj) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof Field field) {
            return tryGetFirst(
                    field::getGenericType,
                    field::getType
            ).orElse(null);
        }
        if (obj instanceof Method method) {
            return tryGetFirst(method::getGenericReturnType, method::getReturnType).orElse(null);
        }
        if (obj instanceof Parameter parameter) {
            return tryGetFirst(parameter::getParameterizedType, parameter::getType).orElse(null);
        }
        if (obj instanceof ParameterizedType parameterizedType) {
            return parameterizedType;
        }
        if (obj instanceof Class<?> clazz) {
            return clazz;
        }
        if (obj instanceof AnnotatedType annotatedType) {
            return tryGetFirst(
                    annotatedType::getType
            ).orElse(null);
        }
        if (obj instanceof Type type) {
            return type;
        }

        return null;
    }

    /**
     * Attempts to get the name of the object.
     * if it is a class, it returns the name of the class,
     * if that does not exist it returns the canonical name of the class,
     * if that does not exist it returns the type name of the class,
     * if that does not exist it returns the simple name of the class,
     * if that does not exist it returns the null,
     * if it is a field, it returns the name of the field, as well as the remapped name if it exists,
     * if it is a method, it returns the name of the method, as well as the remapped name if it exists,
     * if it is a parameter, it returns the name of the parameter,
     * otherwise it returns the toString() of the object.
     *
     * @param obj Object to get the name of.
     * @return Name of the object. Null if the object's name is not present.
     */
    public static String safeUnwrapName(final Object obj) {
        if (null == obj) {
            return null;
        }
        if (obj instanceof String out) {
            if (out.isBlank()) {
                return null;
            } else {
                return removeGenericsAndArrays(out);
            }
        }
        if (obj instanceof Field field) {
            return removeGenericsAndArrays(safeRemap(field));
        }
        if (obj instanceof Method method) {
            return removeGenericsAndArrays(safeRemap(method));
        }
        if (obj instanceof Parameter parameter) {
            return safeUnwrapName(
                    tryGetFirst(
                            parameter::getName
                    ).orElse(null));
        }
        if (obj instanceof Type type) {
            while ((type instanceof GenericArrayType) || (type instanceof Class<?> clazz && clazz.isArray())) {
                while (type instanceof GenericArrayType genericArrayType) {
                    type = genericArrayType.getGenericComponentType();
                }
                while (type instanceof Class<?> clazz && clazz.isArray()) {
                    type = clazz.getComponentType();
                }
            }

            return removeGenericsAndArrays(safeUniqueTypeName(type));
        }
        return removeGenericsAndArrays(obj.toString());
    }

    public static String removeGenericsAndArrays(final String name) {
        if (null == name) {
            return null;
        }
        if (name.isBlank()) {
            return name;
        }
        int arrayIndex = name.indexOf('[');
        if (0 > arrayIndex) {
            arrayIndex = name.length();
        }
        int genericIndex = name.indexOf('<');
        if (0 > genericIndex) {
            genericIndex = name.length();
        }

        return name.substring(0, Math.min(arrayIndex, genericIndex));
    }

    public static String safeRemap(final Method method) {
        if (null == method) {
            return null;
        }
        final var name = tryGet(method::getName);
        if (getRemap().isEmpty()) {
            if (name.isEmpty()) {
                return null;
            }
            if (name.get().isBlank()) {
                return null;
            }
            return name.get();
        }
        final var remap = getRemap().get().getMappedMethod(method.getDeclaringClass(), method);
        if (remap.isBlank()) {
            return name.get();
        }
        return remap;
    }

    public static String safeRemap(final Field field) {
        if (null == field) {
            return null;
        }
        final var name = tryGet(field::getName);
        if (getRemap().isEmpty()) {
            if (name.isEmpty()) {
                return null;
            }
            if (name.get().isBlank()) {
                return null;
            }
            return name.get();
        }
        final var remap = getRemap().get().getMappedField(field.getDeclaringClass(), field);
        if (remap.isBlank()) {
            return name.get();
        }
        return remap;
    }

    public static String safeRemap(final Class<?> clazz) {
        if (null == clazz) {
            return null;
        }
        final var name = tryGet(clazz::getName);
        if (getRemap().isEmpty() || name.isEmpty()) {
            return safeUniqueTypeName(clazz);
        }
        final var remap = getRemap().get().getMappedClass(clazz);
        if (remap.isBlank()) {
            return safeUniqueTypeName(clazz);
        }
        return remap;
    }


    // tryGet(Object::toString) -> Optional<String>
    // tryGet(Method::getFields) -> Optional<Field[]>
    public static <T> Optional<T> tryGet(final Supplier<T> supplier) {
        if (null == supplier) {
            return Optional.empty();
        }
        try {
            return Optional.of(supplier.get());
        } catch (final Throwable e) {
            return Optional.empty();
        }
    }

    @SafeVarargs
    public static <D> Optional<D> tryGetFirst(final Supplier<D>... suppliers) {
        if (null == suppliers) {
            return Optional.empty();
        }
        for (final Supplier<D> supplier : suppliers) {
            final var out = tryGet(supplier);
            if (out.isPresent()) {
                return out;
            }
        }
        return Optional.empty();
    }

    public static boolean filterBounds(Type[] bounds) {
        return null != bounds && bounds.length > 0;
    }

    public static Type[] getAllNonObjects(Type[] bounds) {
        if (null == bounds || bounds.length == 0) {
            return new Type[]{};
        }
        return Arrays.stream(bounds).filter((bound) -> bound != Object.class).toArray(Type[]::new);
    }

    public static String getNestedTypeName(Type type, Set<String> typeVariables) {
        if (null == type) {
            return null;
        }
        if (typeVariables == null) {
            typeVariables = new HashSet<>();
        }
        if (type instanceof TypeVariable<?> variable) {
            if (typeVariables.contains(variable.getName())) {
                return variable.getName();
            }
            typeVariables.add(variable.getName());
            final var variableType = tryGet(variable::getBounds).filter(SafeOperations::filterBounds).map(SafeOperations::getAllNonObjects).filter(
                    SafeOperations::filterBounds);
            final StringJoiner spacedJoiner = new StringJoiner(" ");
            spacedJoiner.add(variable.getName());
            if (variableType.isEmpty()) {
                return spacedJoiner.toString();
            }
            spacedJoiner.add("extends");
            spacedJoiner.add(getJoinedBound(variableType.get(), typeVariables));
            return spacedJoiner.toString();
        }
        if (type instanceof WildcardType wildcardType) {
            final var lowerBounds = tryGet(wildcardType::getLowerBounds).filter(SafeOperations::filterBounds);
            final var upperBounds = tryGet(wildcardType::getUpperBounds).filter(SafeOperations::filterBounds).map(
                    SafeOperations::getAllNonObjects).filter(SafeOperations::filterBounds);
            final StringJoiner boundsJoiner = new StringJoiner(" ");
            boundsJoiner.add("?");
            if (lowerBounds.isPresent()) {
                boundsJoiner.add("super");
                boundsJoiner.add(getJoinedBound(lowerBounds.get(), typeVariables));
                return boundsJoiner.toString();
            }
            if (upperBounds.isEmpty()) {
                return boundsJoiner.toString();
            }
            boundsJoiner.add("extends");
            boundsJoiner.add(getJoinedBound(upperBounds.get(), typeVariables));
            return boundsJoiner.toString();
        }
        if (type instanceof GenericArrayType genericArrayType) {
            final var componentType = tryGet(genericArrayType::getGenericComponentType).orElse(null);
            if (null == componentType) {
                LOG.warn("Unable to get component type of generic array type: {}", genericArrayType);
                return null;
            }
            int arraySize = 0;
            Type nonGenericArrayType = genericArrayType;
            while (nonGenericArrayType instanceof GenericArrayType arrayType) {
                nonGenericArrayType = arrayType.getGenericComponentType();
                arraySize++;
            }
            return getNestedTypeName(nonGenericArrayType, typeVariables) + "[]".repeat(arraySize);
        }

        if (type instanceof ParameterizedType parameterizedType) {
            final StringJoiner joiner = new StringJoiner("");
            final var leftHalf = tryGet(parameterizedType::getOwnerType);
            final var rightHalfNoGenerics = tryGet(parameterizedType::getRawType).map((rawType) -> {
                if (rawType instanceof Class<?> clazz) {
                    return clazz;
                }
                LOG.warn("Unable to get raw type of parameterized type: {}", parameterizedType);
                return null;
            });
            final var rightHalfGenerics = tryGet(parameterizedType::getActualTypeArguments).filter((arguments) -> arguments.length > 0);
            // If the right half is empty, then we can't get the full name of the type.
            if (rightHalfNoGenerics.isEmpty()) {
                LOG.warn("Unable to get raw type of parameterized type: {}", parameterizedType);
                return null;
            }
            if (rightHalfGenerics.isEmpty() && leftHalf.isEmpty()) {
                LOG.warn("Unable to get generic arguments of parameterized type: {}", parameterizedType);
                return null;
            }
            // If the left half is present, then we need to add it to the joiner.
            if (leftHalf.isPresent()) {
                joiner.add(getNestedTypeName(leftHalf.get(), typeVariables));
                joiner.add("$");
                var nonArray = getNonArrayClass(rightHalfNoGenerics.get());
                var remappedName = getRemappedClassName(nonArray);
                if (remappedName == null) {
                    LOG.warn("Unable to get name of class: {}", nonArray);
                    return null;
                }
                if (remappedName.equalsIgnoreCase(nonArray.getName())) {
                    remappedName = nonArray.getSimpleName();
                }
                joiner.add(remappedName);
            } else {
                joiner.add(getNestedTypeName(rightHalfNoGenerics.get(), typeVariables));
            }
            if (rightHalfGenerics.isPresent()) {
                joiner.add("<");
                final var commaJoiner = new StringJoiner(",");
                for (var variable : rightHalfGenerics.get()) {
                    final var name = getNestedTypeName(variable, typeVariables);
                    if (null == name) {
                        LOG.warn("Unable to get name of generic type: {} ({})", variable, type.getTypeName());
                        return null;
                    }
                    commaJoiner.add(name);
                }
                joiner.merge(commaJoiner);
                joiner.add(">");
            }
            for (int i = 0; i < getArraySize(rightHalfNoGenerics.get()); i++) {
                joiner.add("[]");
            }
            return joiner.toString();
        }

        if (!(type instanceof Class<?> clazz)) {
            LOG.warn("Unknown Type: {} ({} [{}])", type, type.getTypeName(), type.getClass());
            return null;
        }
        final StringJoiner joiner = new StringJoiner("");
        final int arraySize = getArraySize(clazz);
        var name = getRemappedClassName(getNonArrayClass(clazz));
        if (name == null) {
            LOG.warn("Unable to get name of class: {}", clazz);
            return null;
        }
        joiner.add(name);

        for (int i = 0; i < arraySize; i++) {
            joiner.add("[]");
        }
        return joiner.toString();
    }

    public static String getRemappedClassName(Class<?> clazz) {
        var name = tryGet(clazz::getName).orElse(null);
        if (getRemap().isPresent()) {
            final var remapped = getRemap().get().getMappedClass(clazz);
            if (null != remapped && !remapped.isBlank()) {
                return remapped;
            }
        }
        return name;
    }

    private static int getArraySize(Class<?> nonArrayClass) {
        int arraySize = 0;
        while (nonArrayClass.isArray()) {
            arraySize++;
            nonArrayClass = nonArrayClass.getComponentType();
        }
        return arraySize;
    }

    private static Class<?> getNonArrayClass(Class<?> clazz) {
        while (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return clazz;
    }

    public static String getJoinedBound(Type[] bounds, Set<String> seenVariables) {
        if (null == bounds || bounds.length == 0) {
            return "";
        }
        return Arrays.stream(bounds).map((bound) -> getNestedTypeName(bound, seenVariables)).collect(Collectors.joining(" & "));
    }

}
