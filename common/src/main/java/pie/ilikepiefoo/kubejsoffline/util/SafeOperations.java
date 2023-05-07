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
import java.util.Optional;
import java.util.StringJoiner;
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
		int arraySize = 0;
		final StringJoiner joiner = new StringJoiner("");
		if (type instanceof TypeVariable<?> variable) {
			final var bounds = tryGet(variable::getBounds).orElse(null);
			if (null != bounds && 0 < bounds.length) {
				if (1 != bounds.length) {
					joiner.add("?").add("extends");
					for (int i = 0; i < bounds.length; i++) {
						joiner.add(bounds[i].getTypeName());
						if (i < bounds.length - 1) {
							joiner.add("&");
						}
					}
				} else {
					joiner.add(bounds[0].getTypeName());
				}
			} else {
				joiner.add("java.lang.Object");
			}
			return joiner.toString();
		} else if (type instanceof WildcardType wild) {
			final var lowerBounds = tryGet(wild::getLowerBounds).orElse(null);
			final var upperBounds = tryGet(wild::getUpperBounds).orElse(null);
			final StringJoiner boundsJoiner = new StringJoiner(" ");
			boundsJoiner.add("?");
			if (null == lowerBounds && null == upperBounds) {
				return boundsJoiner.toString();
			}
			if (null == upperBounds) {
				if (0 == lowerBounds.length) {
					return boundsJoiner.toString();
				}
				if (Object.class != lowerBounds[0]) {
					boundsJoiner.add("super");
					boundsJoiner.add(lowerBounds[0].getTypeName());
				}
			} else {
				if (0 < upperBounds.length) {
					boundsJoiner.add("extends");
					boundsJoiner.add(upperBounds[0].getTypeName());
				}
			}
			return boundsJoiner.toString();
		} else if (type instanceof Class<?> clazz) {
			while (clazz.isArray()) {
				arraySize++;
				clazz = clazz.getComponentType();
			}
			type = clazz;
			final var name = tryGet(clazz::getName);
			if (name.isEmpty() || name.get().isBlank()) {
				return null;
			}
			if (getRemap().isPresent()) {
				final var remapped = getRemap().get().getMappedClass(clazz);
				if (null != remapped && !remapped.isBlank()) {
					joiner.add(remapped);
				} else {
					joiner.add(name.get());
				}
			} else {
				joiner.add(name.get());
			}
		} else if (type instanceof GenericArrayType genericArrayType) {
			final var componentType = tryGet(genericArrayType::getGenericComponentType).orElse(null);
			if (null == componentType) {
				return null;
			}
			joiner.add(safeUniqueTypeName(componentType));
			arraySize++;
		} else if (type instanceof ParameterizedType parameterizedType) {
			final var rawType = tryGet(parameterizedType::getRawType).orElse(null);
			if (null == rawType) {
				return null;
			}
			final var args = tryGet(parameterizedType::getActualTypeArguments).orElse(null);
			if (null == args) {
				return null;
			}
			if (0 == joiner.length()) {
				joiner.add(rawType.getTypeName());
			}
			if (0 < args.length) {
				final StringJoiner paramJoiner = new StringJoiner(",");
				for (final Type ptype : args) {
					final var out = safeUniqueTypeName(ptype);
					if (null == out) {
						return null;
					}
					paramJoiner.add(out);
				}
				joiner.add("<" + paramJoiner + ">");
			}
			return joiner.toString();
		}

		for (int i = 0; i < arraySize; i++) {
			joiner.add("[]");
		}
		return joiner.toString();
	}

	public static Type safeUnwrapReturnType(final Object obj) {
		if (null == obj) {
			return null;
		}
		if (obj instanceof Field field) {
			return (Type) tryGetFirst(
					field::getGenericType,
					field::getType
			).orElse(null);
		}
		if (obj instanceof Method method) {
			return (Type) (
					tryGetFirst(
							method::getGenericReturnType,
							method::getReturnType
					).orElse(null));
		}
		if (obj instanceof Parameter parameter) {
			return (Type) (
					tryGetFirst(
							parameter::getParameterizedType,
							parameter::getType
					).orElse(null));
		}
		if (obj instanceof ParameterizedType parameterizedType) {
			return parameterizedType;
		}
		if (obj instanceof Class<?> clazz) {
			return clazz;
		}
		if (obj instanceof AnnotatedType annotatedType) {
			return (Type) tryGetFirst(
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

	public static Optional<?> tryGetFirst(final Supplier<?>... suppliers) {
		if (null == suppliers) {
			return Optional.empty();
		}
		for (final Supplier<?> supplier : suppliers) {
			final var out = tryGet(supplier);
			if (out.isPresent()) {
				return out;
			}
		}
		return Optional.empty();
	}

}
