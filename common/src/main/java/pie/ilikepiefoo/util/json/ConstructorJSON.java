package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;

public class ConstructorJSON {
	public static JsonObject of(Constructor<?> constructor) {
		JsonObject object = new JsonObject();
		ExecutableJSON.attach(object, constructor);
		return object;
	}

	public static JsonArray of(Constructor<?>[] constructors) {
		if(constructors == null)
			return new JsonArray();
		JsonArray array = new JsonArray();
		for(var constructor : constructors)
			array.add(of(constructor));
		return array;
	}
}
