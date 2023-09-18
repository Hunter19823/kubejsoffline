package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.function.Supplier;


public class ClassJSON {

	private static final Logger LOG = LogManager.getLogger();

	public static void of(@Nullable Type sub) {
		if(sub == null)
			return;
		Class<?> subject = null;
		if(sub instanceof Class<?>)
			subject = (Class<?>) sub;
		if(subject == null) {
			TypeJSON.of(sub);
			return;
		}
		JsonObject object = TypeJSON.of(subject);
		if (object == null) {
			return;
		}

		// TODO: Do not include any other data if the class is an array.
		// Instead, include the component type, and a reference to the array class.
//		if(subject.isArray()) {
//			while(subject.isArray()) {
//				subject = subject.getComponentType();
//			}
//			object.addProperty();
//		}

		attachType(object, JSONProperty.SUPER_CLASS.jsName, subject::getSuperclass);
		attachType(object, JSONProperty.GENERIC_SUPER_CLASS.jsName, subject::getGenericSuperclass);
		attachTypes(object, JSONProperty.INTERFACES.jsName, subject::getInterfaces);
		attachTypes(object, JSONProperty.GENERIC_INTERFACES.jsName, subject::getGenericInterfaces);
		PackageJSONManager.getInstance().attachPackageData(object, subject);

		// Add Annotations
		var array = AnnotationJSON.of(subject);
		if (!array.isEmpty()) {
			object.add(JSONProperty.ANNOTATIONS.jsName, array);
		}

		// Add modifiers
		object.addProperty(JSONProperty.MODIFIERS.jsName, subject.getModifiers());

		// Add Fields
		array = FieldJSON.of(SafeOperations.tryGetFirst(
				subject::getDeclaredFields
		).orElse(null));
		if (!array.isEmpty())
			object.add(JSONProperty.FIELDS.jsName, array);

		// Add Methods
		array = MethodJSON.of(SafeOperations.tryGetFirst(
				subject::getDeclaredMethods
		).orElse(null));
		if (!array.isEmpty())
			object.add(JSONProperty.METHODS.jsName, array);

		// Add Constructors
		array = ConstructorJSON.of(SafeOperations.tryGetFirst(
				subject::getDeclaredConstructors
		).orElse(null));
		if (!array.isEmpty())
			object.add(JSONProperty.CONSTRUCTORS.jsName, array);
	}

	public static void attachTypes(@Nonnull JsonObject object, @Nonnull String key, @Nullable Supplier<Type[]> typeSuppliers) {
		SafeOperations.tryGet(typeSuppliers).ifPresent(types -> {
			JsonArray array = new JsonArray();
			for (Type type : types) {
				var temp = TypeJSON.of(type);
				if (temp != null && temp.has(JSONProperty.TYPE_ID.jsName))
					array.add(temp.get(JSONProperty.TYPE_ID.jsName).getAsInt());
			}
			if (!array.isEmpty())
				object.add(key, array);
		});
	}

	public static void attachType(@Nonnull JsonObject object, @Nonnull String key, @Nullable Supplier<Type> typeSupplier) {
		SafeOperations.tryGet(typeSupplier).ifPresent(type -> {
			var temp = TypeJSON.of(type);
				if (temp != null && temp.has(JSONProperty.TYPE_ID.jsName))
					object.addProperty(key, temp.get(JSONProperty.TYPE_ID.jsName).getAsInt());
		});
	}


}
