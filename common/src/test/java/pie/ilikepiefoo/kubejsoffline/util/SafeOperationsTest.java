package pie.ilikepiefoo.kubejsoffline.util;

import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

class SafeOperationsTest {
    @ExpectedTypeName("org.apache.logging.log4j.Logger")
    public static final Logger LOG = LogManager.getLogger();


    @Test
    void safeUnwrapReturnTypeName() {
        final String methodFormat = "Method '%s' has return type '%s' which maps to '%s'%n";
        final String parameterFormat = "Parameter '%s' maps to '%s'%n";
        for (Field declaredField : WildcardGenerics.class.getDeclaredFields()) {
            LOG.info("========================================");
            LOG.info("Testing field '{}'", declaredField.toGenericString());
            logTypeDebugInfo(SafeOperations.safeUnwrapReturnType(declaredField));
            LOG.info(String.format(methodFormat, declaredField.getName(), declaredField.getType().getCanonicalName(),
                    SafeOperations.safeUnwrapReturnTypeName(declaredField)));
            Class<?> type = declaredField.getType();
            if (declaredField.getGenericType() instanceof Class<?> clazz) {
                type = clazz;
            }
            for (final Method method : type.getDeclaredMethods()) {
                LOG.info("========================================");
                LOG.info("Testing method '{}'", method.toGenericString());
                logTypeDebugInfo(SafeOperations.safeUnwrapReturnType(method));
                LOG.info(String.format(methodFormat, method.getName(), method.getReturnType().getCanonicalName(),
                        SafeOperations.safeUnwrapReturnTypeName(method)));
                for (final var parameter : method.getParameters()) {
                    logTypeDebugInfo(SafeOperations.safeUnwrapReturnType(parameter));
                    LOG.info(String.format(parameterFormat, parameter.getType().getName(), SafeOperations.safeUnwrapReturnTypeName(parameter)));
                }
            }
        }
    }

    private void logTypeDebugInfo(Type type) {
        StringJoiner joiner = new StringJoiner(", ");
        if (type instanceof GenericArrayType) {
            joiner.add("GenericArrayType");
        }
        if (type instanceof Class) {
            joiner.add("Class");
        }
        if (type instanceof TypeVariable<?> variable) {
            joiner.add("TypeVariable(" + variable.getName() + ")");
        }
        if (type instanceof WildcardType wildcard) {
            joiner.add("WildcardType");
        }
        if (type instanceof ParameterizedType parameterized) {
            joiner.add("ParameterizedType");
        }
        LOG.info("Type '{}' Implements: '{}'", type.getTypeName(), joiner);
    }

    @Test
    void allWildcards() {
        List<Throwable> exceptions = new ArrayList<>();
        for (Field field : WildcardGenerics.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(ExpectedTypeName.class)) {
                try {
                    testField(field, field.getAnnotation(ExpectedTypeName.class).value());
                } catch (Throwable e) {
                    exceptions.add(e);
                }
            } else {
                exceptions.add(new AssertionError(String.format("Field '%s' is missing annotation '%s'", field, ExpectedTypeName.class)));
            }
        }
        if (exceptions.isEmpty()) {
            return;
        }
        for (Throwable e : exceptions) {
            LOG.error(e);
        }
        throw new AssertionError(String.format("Failed %d tests", exceptions.size()));
    }

    private void testField(Field field, String expected) {
        LOG.info("Testing field '{}'", field);
        var type = SafeOperations.safeUnwrapReturnType(field);
        LOG.info("Type: '{}'", type);
        var name = SafeOperations.safeUniqueTypeName(type);
        LOG.info("Name: '{}'", name);
        assertEquals(name, expected);
    }

    private void assertEquals(Object a, Object b) {
        assert a.equals(b) : String.format("Expected '%s' to equal '%s'", a, b);
    }

    @Test
    void testNestedSubclassName() {
        logTypeDebugInfo(OreConfiguration.TargetBlockState.class);
        assertEquals(SafeOperations.safeUniqueTypeName(OreConfiguration.TargetBlockState.class),
                "net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration$TargetBlockState");

        logTypeDebugInfo(WorldCarver.CarveSkipChecker.class);
        assertEquals(SafeOperations.safeUniqueTypeName(WorldCarver.CarveSkipChecker.class),
                "net.minecraft.world.level.levelgen.carver.WorldCarver$CarveSkipChecker");
    }
}