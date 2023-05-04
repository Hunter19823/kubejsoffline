package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class TypeJSON {
	private static final Logger LOG = LogManager.getLogger();

	@Nullable
	public static JsonObject of(@Nullable final Type type) {
		if (null == type) {
			return null;
		}
		final String name = SafeOperations.safeUnwrapName(type);
		if (null == name || name.isBlank()) {
			return null;
		}

		final JsonObject object = ClassJSONManager.getInstance().getTypeData(type);
		if (null == object) {
			return null;
		}
		try {
			attachGenericAndArrayData(object, type);

			object.addProperty(JSONProperty.BASE_CLASS_NAME.jsName, name);
		} catch (final IllegalStateException e) {
			LOG.error("Failed to attach generic and array data to type '{}' ('{}')", name, type);
			LOG.info(object);
			throw e;
		}


		return object;
	}

	public static void attachGenericAndArrayData(@Nonnull final JsonObject object, @Nullable final Supplier<Type> typeSupplier) {
		SafeOperations.tryGet(typeSupplier).ifPresent((type) -> attachGenericAndArrayData(object, type));
	}

	public static void attachGenericAndArrayData(@Nonnull final JsonObject object, @Nullable final Type type) {
		if (null == type) {
			return;
		}
		int arrayDepth = 0;
		if (type instanceof Class<?> subject) {
			if (subject.isArray()) {
				while (subject.isArray()) {
					arrayDepth++;
					subject = subject.getComponentType();
				}
				attachGenericAndArrayData(object, subject);
			}
		} else if(type instanceof GenericArrayType genericArrayType) {
			final var comp = SafeOperations.tryGet(genericArrayType::getGenericComponentType);
			if(comp.isPresent()){
				attachGenericAndArrayData(object, comp.get());
				arrayDepth++;
			}
		}
		if (type instanceof ParameterizedType parameterizedType) {
			SafeOperations.tryGet(parameterizedType::getRawType)
					.ifPresent((rawType) -> {
						final var typeObject = of(rawType);
						if (null != typeObject && 0 != typeObject.size()) {
							object.addProperty(
									JSONProperty.RAW_PARAMETERIZED_TYPE.jsName,
									typeObject.get(JSONProperty.TYPE_ID.jsName).getAsInt());
						}
					});
			addGenericData(object, parameterizedType::getActualTypeArguments);
		}


		if (0 < arrayDepth) {
			object.addProperty(JSONProperty.ARRAY_DEPTH.jsName, arrayDepth);
		}
	}

	public static void addGenericData(@Nonnull final JsonObject obj, @Nullable final Supplier<Type[]> typeArgs) {
		final var arguments = new JsonArray();
		final var typeArguments = SafeOperations.tryGet(typeArgs);
		if (typeArguments.isEmpty()) {
			return;
		}
		for (final var typeArgument : typeArguments.get()) {
			final var argument = of(typeArgument);
			if (null == argument) {
				return;
			}
			if (0 < argument.size()) {
				arguments.add(argument.get(JSONProperty.TYPE_ID.jsName).getAsInt());
			}
		}
		if (0 < arguments.size()) {
			if (obj.has(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName)) {
				// Check if the arguments are the same.
				final var existingArguments = obj.get(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName).getAsJsonArray();
				if (existingArguments.size() != arguments.size()) {
					throw new IllegalStateException("Parameterized arguments are being overwritten!");
				}
				for (int i = 0; i < existingArguments.size(); i++) {
					if (existingArguments.get(i).getAsInt() != arguments.get(i).getAsInt()) {
						LOG.info("Type '{}' has different parameterized arguments!", obj.get(JSONProperty.TYPE_ID.jsName).getAsInt());
						LOG.info("Existing: {}", existingArguments);
						LOG.info("New: {}", arguments);
						for (int j = 0; j < existingArguments.size(); j++) {
							LOG.info("Existing[{}]: {}", j, ClassJSONManager.getInstance().getTypeData().get(existingArguments.get(j).getAsInt()));
						}
						for (int j = 0; j < arguments.size(); j++) {
							LOG.info("New[{}]: {}", j, ClassJSONManager.getInstance().getTypeData().get(arguments.get(j).getAsInt()));
						}

						throw new IllegalStateException("Parameterized arguments are being overwritten!");
					}
				}
			}
			obj.add(JSONProperty.PARAMETERIZED_ARGUMENTS.jsName, arguments);
		}
	}
}
