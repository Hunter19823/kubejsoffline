package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.function.Supplier;


public class ClassJSON {

	private static final Logger LOG = LogManager.getLogger();

	public static void of(Class<?> subject) {
		if(subject == null)
			return;
		JsonObject object = TypeJSON.of(subject);
		if(object == null)
			return;

		attachType(object, "superclass", subject::getSuperclass);
		attachType(object, "genericSuperclass", subject::getGenericSuperclass);
		attachTypes(object, "interfaces", subject::getInterfaces);
		attachTypes(object, "genericInterfaces", subject::getGenericInterfaces);
		SafeOperations.tryGet(subject::getPackageName).ifPresent(s -> object.addProperty("packageName", s));

		// Add Annotations
		var array = AnnotationJSON.of(subject);
		if(array.size() > 0)
			object.add("ano", array);

		// Add modifiers
//		var modifiers = ModifierJSON.of(subject.getModifiers());
//		if(modifiers.size() > 0)
//			object.add("mod", modifiers);
		object.addProperty("mod", subject.getModifiers());

		// Add Fields
		array = FieldJSON.of((Field[]) SafeOperations.tryGetFirst(
				subject::getDeclaredFields,
				subject::getFields
		).orElse(null));
		if(array.size() > 0)
			object.add("fields", array);

		// Add Methods
		array = MethodJSON.of((Method[]) SafeOperations.tryGetFirst(
				subject::getDeclaredMethods,
				subject::getMethods
		).orElse(null));
		if(array.size() > 0)
			object.add("meth", array);

		// Add Constructors
		array = ConstructorJSON.of((Constructor<?>[]) SafeOperations.tryGetFirst(
				subject::getDeclaredConstructors,
				subject::getConstructors
		).orElse(null));
		if(array.size() > 0)
			object.add("cons", array);

	}

	public static void attachTypes(JsonObject object, String key, Supplier<Type[]> typeSuppliers) {
		SafeOperations.tryGet(typeSuppliers).ifPresent(types -> {
			JsonArray array = new JsonArray();
			for (Type type : types) {
				var temp = TypeJSON.of(type);
				if (temp != null)
					array.add(temp.get("id").getAsInt());
			}
			object.add(key, array);
		});
	}

	public static void attachType(JsonObject object, String key, Supplier<Type> typeSupplier) {
		SafeOperations.tryGet(typeSupplier).ifPresent(type -> {
			var temp = TypeJSON.of(type);
			if(temp != null)
				object.addProperty(key, temp.get("id").getAsInt());
		});
	}


}
