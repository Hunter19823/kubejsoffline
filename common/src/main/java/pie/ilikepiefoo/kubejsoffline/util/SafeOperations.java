package pie.ilikepiefoo.kubejsoffline.util;

import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class SafeOperations {
    private static final Logger LOG = LogManager.getLogger();
    private static boolean loadedRemap;
    private static MinecraftRemapper remapper;

    public static boolean isClassPresent(Class<?> type) {
        if (type == null) {
            return true;
        }
        try {
            Object result = null;
            result = type.getName();
            result = type.getCanonicalName();
            result = type.getModifiers();
            result = type.getPackage();
            if (!isClassPresent(type.getSuperclass())) {
                return false;
            }
            for (var typeParameter : type.getTypeParameters()) {
                if (!isTypeVariablePresent(typeParameter)) {
                    return false;
                }
            }
            result = type.getInterfaces();
            for (var interfaceType : type.getInterfaces()) {
                if (!isClassPresent(interfaceType)) {
                    return false;
                }
            }
            result = type.getGenericInterfaces();
            result = type.getAnnotations();
            return true;
        } catch (final Throwable e) {
            LOG.warn("Skipping Class that isn't fully loaded...", e);
            return false;
        }
    }

    public static boolean isTypePresent(Type type) {
        if (type == null) {
            return true;
        }
        try {
            type.getTypeName();
            if (type.getTypeName().isBlank()) {
                return false;
            }
            if (type instanceof Class<?> clazz) {
                return isClassPresent(clazz);
            }
            if (type instanceof TypeVariable<?> typeVariable) {
                return isTypeVariablePresent(typeVariable);
            }
            if (type instanceof ParameterizedType parameterizedType) {
                for (var argument : parameterizedType.getActualTypeArguments()) {
                    if (!isTypePresent(argument)) {
                        return false;
                    }
                }
                return isTypePresent(parameterizedType.getRawType());
            }
            if (type instanceof WildcardType wildcardType) {
                for (var bound : wildcardType.getUpperBounds()) {
                    if (!isTypePresent(bound)) {
                        return false;
                    }
                }
                for (var bound : wildcardType.getLowerBounds()) {
                    if (!isTypePresent(bound)) {
                        return false;
                    }
                }
            }

            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    public static boolean isMethodPresent(Method method) {
        if (method == null) {
            return true;
        }
        return isClassPresent(method.getReturnType()) && isExecutableLoaded(method);
    }

    public static boolean isFieldPresent(Field field) {
        if (field == null) {
            return true;
        }
        try {
            isClassPresent(field.getType());
            Object obj = field.getGenericType();
            field.getAnnotations();
            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    public static boolean isConstructorPresent(Constructor<?> constructor) {
        if (constructor == null) {
            return true;
        }
        return isExecutableLoaded(constructor);
    }

    public static boolean isTypeVariablePresent(TypeVariable<?> type) {
        if (type == null) {
            return true;
        }
        try {
            type.getTypeName();
            type.getGenericDeclaration();
            type.getName();
            type.getBounds();
            for (var bound : type.getBounds()) {
                bound.getTypeName();
            }
            return true;
        } catch (final Throwable e) {
            return false;
        }
    }

    public static boolean isExecutableLoaded(Executable executable) {
        if (executable == null) {
            return true;
        }
        try {
            executable.getName();
            executable.toGenericString();
            executable.getModifiers();
            for (var typeParameter : executable.getTypeParameters()) {
                if (!isTypeVariablePresent(typeParameter)) {
                    return false;
                }
            }
            for (var parameter : executable.getParameters()) {
                if (!isClassPresent(parameter.getType())) {
                    return false;
                }
                parameter.getParameterizedType();
            }
            for (var parameter : executable.getGenericParameterTypes()) {
                parameter.getTypeName();
            }

            return true;
        } catch (final Throwable e) {
            LOG.warn("Skipping Executable that isn't fully loaded...", e);
            return false;
        }
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

    public static String getRemappedClassName(Class<?> clazz, boolean simple) {
        var name = simple ?
                tryGet(clazz::getSimpleName).orElse(null)
                :
                tryGet(clazz::getName).orElse(null);
        if (getRemap().isPresent()) {
            final var remapped = getRemap().get().getMappedClass(clazz);
            if (null != remapped && !remapped.isBlank()) {
                return remapped;
            }
        }
        return name;
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
            return getRemappedClassName(clazz, true);
        }
        final var remap = getRemap().get().getMappedClass(clazz);
        if (remap.isBlank()) {
            return getRemappedClassName(clazz, true);
        }
        return remap;
    }

    private static Optional<MinecraftRemapper> getRemap() {
        if (!loadedRemap) {
            remapper = tryGet(RemappingHelper::getMinecraftRemapper).orElse(null);
            loadedRemap = true;
        }
        return Optional.ofNullable(remapper);
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

    public static Type[] getAllNonObjects(Type[] bounds) {
        if (null == bounds || bounds.length == 0) {
            return new Type[]{};
        }
        return Arrays.stream(bounds).filter((bound) -> bound != Object.class).toArray(Type[]::new);
    }

}
