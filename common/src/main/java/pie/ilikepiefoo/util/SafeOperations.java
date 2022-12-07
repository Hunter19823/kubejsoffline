package pie.ilikepiefoo.util;

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
import java.lang.reflect.WildcardType;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Supplier;

public class SafeOperations {
	private static final Logger LOG = LogManager.getLogger();
	private static boolean loadedRemap = false;
	private static MinecraftRemapper remapper = null;
	private static Optional<MinecraftRemapper> getRemap() {
		if(!loadedRemap){
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
	 * @param obj Object to get the name of.
	 * @return Name of the object.
	 */
	public static String safeUnwrapReturnTypeName(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof String out)
			if(out.isBlank())
				return null;
			else
				return out;
		if(obj instanceof Field field) {
			return safeUnwrapReturnTypeName(
					tryGetFirst(
							field::getGenericType,
							field::getType,
							field::getAnnotatedType
					).orElse(null));
		}
		if(obj instanceof Method method) {
			return safeUnwrapReturnTypeName(
					tryGetFirst(
							method::getGenericReturnType,
							method::getReturnType,
							method::getAnnotatedReturnType
					).orElse(null));
		}
		if(obj instanceof Parameter parameter) {
			return safeUnwrapReturnTypeName(
					tryGetFirst(
							parameter::getParameterizedType,
							parameter::getType,
							parameter::getAnnotatedType
					).orElse(null));
		}
		if(obj instanceof ParameterizedType parameterizedType){
			return safeUnwrapReturnTypeName(
					tryGetFirst(
							parameterizedType::getRawType
					).orElse(null));
		}
		if(obj instanceof Type type) {
			return safeUniqueTypeName(type);
		}

		return obj.toString();
	}

	public static String safeUniqueTypeName(Type type) {
		if(type == null)
			return null;
		int arraySize = 0;
		StringJoiner joiner = new StringJoiner("");
		if(type instanceof WildcardType wild) {
			var lowerBounds = tryGet(wild::getLowerBounds).orElse(null);
			var upperBounds = tryGet(wild::getUpperBounds).orElse(null);
			StringJoiner boundsJoiner = new StringJoiner(" ");
			boundsJoiner.add("?");
			if(lowerBounds!=null && lowerBounds.length>0) {
				if(lowerBounds[0] != Object.class) {
					boundsJoiner.add("super");
					boundsJoiner.add(safeUniqueTypeName(lowerBounds[0]));
				}
			} else if(upperBounds!=null && upperBounds.length>0) {
				boundsJoiner.add("extends");
				boundsJoiner.add(safeUniqueTypeName(upperBounds[0]));
			}
			return boundsJoiner.toString();
		}
		if(type instanceof Class<?> clazz) {
			while(clazz.isArray())
			{
				arraySize++;
				clazz = clazz.getComponentType();
			}
			type = clazz;
			var name = tryGet(clazz::getName);
			if(name.isEmpty() || name.get().isBlank())
				return null;
			if(getRemap().isPresent()){
				var remapped = getRemap().get().remapClass(clazz, name.get());
				if(remapped != null && !remapped.isBlank()) {
					joiner.add(remapped);
				}else {
					joiner.add(name.get());
				}
			}else{
				joiner.add(name.get());
			}
		}else if(type instanceof GenericArrayType genericArrayType) {
			var componentType = tryGet(genericArrayType::getGenericComponentType).orElse(null);
			if(componentType == null)
				return null;
			joiner.add(safeUniqueTypeName(componentType));
			arraySize++;
		}
		if(type instanceof ParameterizedType parameterizedType) {
			var rawType = tryGet(parameterizedType::getRawType).orElse(null);
			if(rawType == null)
				return null;
			var args = tryGet(parameterizedType::getActualTypeArguments).orElse(null);
			if(args == null)
				return null;
			joiner.add(safeUniqueTypeName(rawType));
			if(args.length > 0) {
				StringJoiner paramJoiner = new StringJoiner(",");
				for (Type ptype : args) {
					var out = safeUniqueTypeName(ptype);
					if(out == null)
						return null;
					paramJoiner.add(out);
				}
				joiner.add("<"+paramJoiner.toString()+">");
			}
			return safeUniqueTypeName(parameterizedType.getRawType());
		}

		for(int i = 0; i < arraySize; i++)
			joiner.add("[]");
		return joiner.toString();
	}

	public static Type safeUnwrapReturnType(Object obj) {
		if(obj == null)
			return null;
		if(obj instanceof Field field) {
			return (Type) tryGetFirst(
							field::getType,
							field::getAnnotatedType,
							field::getGenericType
					).orElse(null);
		}
		if(obj instanceof Method method) {
			return (Type) (
					tryGetFirst(
							method::getReturnType,
							method::getAnnotatedReturnType,
							method::getGenericReturnType
					).orElse(null));
		}
		if(obj instanceof Parameter parameter) {
			return (Type) (
					tryGetFirst(
							parameter::getParameterizedType,
							parameter::getType,
							parameter::getAnnotatedType
					).orElse(null));
		}
		if(obj instanceof ParameterizedType parameterizedType){
			return parameterizedType;
		}
		if(obj instanceof Class<?> clazz) {
			return clazz;
		}
		if(obj instanceof AnnotatedType annotatedType) {
			return (Type) tryGetFirst(
					annotatedType::getType
			).orElse(null);
		}
		if(obj instanceof Type type) {
			return type;
		}

		return null;
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
			return safeRemap(field);
		}
		if(obj instanceof Method method) {
			return safeRemap(method);
		}
		if(obj instanceof Parameter parameter) {
			return safeUnwrapName(
					tryGetFirst(
							parameter::getName
					).orElse(null));
		}
		if(obj instanceof Type type) {
			while((type instanceof GenericArrayType) || (type instanceof Class<?> clazz && clazz.isArray())){
				while (type instanceof GenericArrayType genericArrayType)
					type = genericArrayType.getGenericComponentType();
				while (type instanceof Class<?> clazz && clazz.isArray())
					type = clazz.getComponentType();
			}

			return safeUniqueTypeName(type);
		}
		return obj.toString();
	}

	public static String safeRemap(Method method) {
		if(method == null)
			return null;
		var name = tryGet(method::getName);
		if(getRemap().isEmpty()) {
			if (name.isEmpty())
				return null;
			if(name.get().isBlank())
				return null;
			return name.get();
		}
		var remap = getRemap().get().remapMethod(method.getDeclaringClass(), method, name.get());
		if(remap.isBlank())
			return name.get();
		return remap;
	}

	public static String safeRemap(Field field) {
		if(field == null)
			return null;
		var name = tryGet(field::getName);
		if(getRemap().isEmpty()) {
			if (name.isEmpty())
				return null;
			if(name.get().isBlank())
				return null;
			return name.get();
		}
		var remap = getRemap().get().remapField(field.getDeclaringClass(), field, name.get());
		if(remap.isBlank())
			return name.get();
		return remap;
	}

	public static String safeRemap(Class<?> clazz) {
		if(clazz == null)
			return null;
		var name = tryGet(clazz::getName);
		if(getRemap().isEmpty() || name.isEmpty()) {
			return safeUniqueTypeName(clazz);
		}
		var remap = getRemap().get().remapClass(clazz, name.get());
		if(remap.isBlank())
			return safeUniqueTypeName(clazz);
		return remap;
	}



	// tryGet(Object::toString) -> Optional<String>
	// tryGet(Method::getFields) -> Optional<Field[]>
	public static <T> Optional<T> tryGet(Supplier<T> supplier) {
		if(supplier == null)
			return Optional.empty();
		try {
			return Optional.of(supplier.get());
		} catch (Throwable e) {
			return Optional.empty();
		}
	}

	public static Optional<?> tryGetFirst(Supplier<?>... suppliers) {
		if(suppliers == null)
			return Optional.empty();
		for (Supplier<?> supplier : suppliers) {
			var out = tryGet(supplier);
			if(out.isPresent())
				return out;
		}
		return Optional.empty();
	}

}
