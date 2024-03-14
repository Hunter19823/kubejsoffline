package pie.ilikepiefoo.kubejsoffline.util;

import dev.latvian.mods.rhino.mod.util.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

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
            return getRemappedClassName(clazz, true);
        }
        final var remap = getRemap().get().getMappedClass(clazz);
        if (remap.isBlank()) {
            return getRemappedClassName(clazz, true);
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

    public static Type[] getAllNonObjects(Type[] bounds) {
        if (null == bounds || bounds.length == 0) {
            return new Type[]{};
        }
        return Arrays.stream(bounds).filter((bound) -> bound != Object.class).toArray(Type[]::new);
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

}
