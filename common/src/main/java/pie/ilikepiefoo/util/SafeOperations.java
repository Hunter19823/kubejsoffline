package pie.ilikepiefoo.util;

import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.util.RemappingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

public class SafeOperations {
	private static final Logger LOG = LogManager.getLogger();

	/**
	 * Returns the full name of the object if it is a class,
	 * An attempt will be made for the generic type before falling back to the raw type.
	 * if it is a field, it returns the return type of the field,
	 * if it is a method, it returns the return type of the method,
	 * if it is a parameter, it returns the type of the parameter,
	 * if it is a parameterized type, it returns the raw type of the parameterized type,
	 * otherwise it returns the toString() of the object.
	 * @param obj Object to get the name of.
	 * @return Name of the object.
	 */
	public static String safeUnwrapReturnType(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof String out)
			if(out.isBlank())
				return null;
			else
				return out;
		if(obj instanceof Field field) {
			return safeUnwrapReturnType(
					tryGetFirst(
							field::getGenericType,
							field::getType,
							field::getAnnotatedType
					).orElse(null));
		}
		if(obj instanceof Method method) {
			return safeUnwrapReturnType(
					tryGetFirst(
							method::getGenericReturnType,
							method::getReturnType,
							method::getAnnotatedReturnType
					).orElse(null));
		}
		if(obj instanceof Parameter parameter) {
			return safeUnwrapReturnType(
					tryGetFirst(
							parameter::getParameterizedType,
							parameter::getType,
							parameter::getAnnotatedType
					).orElse(null));
		}
		if(obj instanceof ParameterizedType parameterizedType){
			return safeUnwrapReturnType(
					tryGetFirst(
							parameterizedType::getRawType
					).orElse(null));
		}
		if(obj instanceof Class<?> clazz) {
			return safeUnwrapReturnType(
					tryGetFirst(
							clazz::getName,
							clazz::getCanonicalName,
							clazz::getTypeName
					).orElse(null));
		}
		if(obj instanceof Type type) {
			return safeUnwrapReturnType(
					tryGetFirst(
							type::getTypeName
					).orElse(null));
		}
		if(obj instanceof AnnotatedType annotatedType) {
			return safeUnwrapReturnType(
					tryGetFirst(
							annotatedType::getType
					).orElse(null));
		}

		return obj.toString();
	}

	/**
	 * Attempts to get the name of the object.
	 * if it is a class, it returns the name of the class,
	 * 		if that does not exist it returns the canonical name of the class,
	 * 		if that does not exist it returns the type name of the class,
	 * 		if that does not exist it returns the simple name of the class,
	 * 		if that does not exist it returns the null,
	 * if it is a field, it returns the name of the field, as well as the remapped name if it exists,
	 * if it is a method, it returns the name of the method, as well as the remapped name if it exists,
	 * if it is a parameter, it returns the name of the parameter,
	 * otherwise it returns the toString() of the object.
	 * @param obj Object to get the name of.
	 * @return Name of the object. Null if the object's name is not present.
	 */
	public static String safeUnwrapName(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof String out)
			if(out.isBlank())
				return null;
			else
				return out;
		if(obj instanceof Field field) {
			var out = tryGet(field::getName);
			if(out.isEmpty()) {
				LOG.warn("Unable to get name of field: " + field);
				return null;
			}
			var remapped = RemappingHelper.getMinecraftRemapper().remapField(field.getDeclaringClass(), field, out.get());
			if(remapped.isBlank())
				return out.get();
			return remapped;
		}
		if(obj instanceof Method method) {
			var out = tryGet(method::getName);
			if(out.isEmpty()) {
				LOG.warn("Unable to get name of method: " + method);
				return null;
			}
			var remapped = RemappingHelper.getMinecraftRemapper().remapMethod(method.getDeclaringClass(), method, out.get());
			if(remapped.isBlank())
				return out.get();
			return remapped;
		}
		if(obj instanceof Parameter parameter) {
			return safeUnwrapName(
					tryGetFirst(
							parameter::getName
					).orElse(null));
		}
		if(obj instanceof Class<?> clazz) {
			var name = tryGet(clazz::getName);
			if(name.isEmpty() || name.get().isBlank())
				return safeUnwrapName(
					tryGetFirst(
							clazz::getCanonicalName,
							clazz::getTypeName,
							clazz::getSimpleName
					).orElse(null));
			var remapped = RemappingHelper.getMinecraftRemapper().remapClass(clazz, name.get());
			if(remapped.isBlank())
				return name.get();
			return remapped;
		}
		if(obj instanceof Type type) {
			return safeUnwrapName(
					tryGetFirst(
							type::getTypeName
					).orElse(null));
		}
		return obj.toString();
	}
	// tryGet(Object::toString) -> Optional<String>
	// tryGet(Method::getFields) -> Optional<Field[]>
	public static <T> Optional<T> tryGet(Supplier<T> supplier) {
		try {
			return Optional.of(supplier.get());
		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	public static Optional<?> tryGetFirst(Supplier<?>... suppliers) {
		for (Supplier<?> supplier : suppliers) {
			var out = tryGet(supplier);
			if(out.isPresent())
				return out;
		}
		return Optional.empty();
	}

	public static void addIfPresent(JsonObject object, String key, Supplier<Type> supplier) {

	}
}
