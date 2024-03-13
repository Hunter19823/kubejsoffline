package pie.ilikepiefoo.kubejsoffline.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.StringJoiner;

public class ReflectionUtils {

    public static int hashCode(final Type type) {
        return getGenericDefinition(type).hashCode();
    }

    public static int hashCode(Type[] types) {
        int[] hashes = Arrays.stream(types).mapToInt(ReflectionUtils::hashCode).toArray();
        return Arrays.hashCode(hashes);
    }

    public static String getGenericDefinition(Type type) {
        return getGenericDefinition(type, new TypeVariableMap(), false);
    }

    private static String getGenericDefinition(Type type, TypeVariableMap typeVariableMap, boolean isDefiningTypeVariable) {
        if (type instanceof TypeVariable<?> variable) {
            type = typeVariableMap.get(variable);
        }
        if (type instanceof Class<?> clazz) {
            return clazz.getCanonicalName();
        }
        if (type instanceof TypeVariable<?> variable) {
            if (isDefiningTypeVariable) {
                return variable.getName();
            }
            var nonObjectBounds = Arrays.stream(variable.getBounds()).filter(b -> b != Object.class).toArray(Type[]::new);
            if (nonObjectBounds.length == 0) {
                return variable.getName();
            }
            StringJoiner joiner = new StringJoiner(" & ", " extends ", "");
            for (Type bound : nonObjectBounds) {
                joiner.add(getGenericDefinition(bound, typeVariableMap, true));
            }
            return variable.getName() + joiner;
        }
        if (type instanceof WildcardType wildcardType) {
            if (wildcardType.getLowerBounds().length > 0 && wildcardType.getLowerBounds()[0] != Object.class) {
                return "? super " + getGenericDefinition(wildcardType.getLowerBounds()[0], typeVariableMap, isDefiningTypeVariable);
            }
            if (wildcardType.getUpperBounds().length > 0 && wildcardType.getUpperBounds()[0] != Object.class) {
                return "? extends " + getGenericDefinition(wildcardType.getUpperBounds()[0], typeVariableMap, isDefiningTypeVariable);
            }
            return "?";
        }
        if (type instanceof GenericArrayType genericArrayType) {
            return getGenericDefinition(genericArrayType.getGenericComponentType(), typeVariableMap, isDefiningTypeVariable) + "[]";
        }
        if (type instanceof ParameterizedType parameterizedType) {
            StringJoiner joiner = new StringJoiner(", ", "<", ">");
            for (var arg : parameterizedType.getActualTypeArguments()) {
                joiner.add(getGenericDefinition(arg, typeVariableMap, isDefiningTypeVariable));
            }
            return getGenericDefinition(parameterizedType.getRawType(), typeVariableMap, isDefiningTypeVariable) + joiner;
        }
        return type.getTypeName();
    }
}
