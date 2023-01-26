package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

public class ConstructorJSON {
	@Nullable
	public static JsonObject of(@Nullable Constructor<?> constructor) {
		JsonObject object = new JsonObject();
		ExecutableJSON.attach(object, constructor);
		if(!object.has(JSONProperty.PARAMETERS.jsName)) // Throw out any constructors that have broken parameters.
			return null;
		return object;
	}

	@Nonnull
	public static JsonArray of(@Nullable Constructor<?>[] constructors) {
		if(constructors == null)
			return new JsonArray();
		JsonArray array = new JsonArray();
		for(var constructor : constructors) {
			var constructorJson = of(constructor);
			if(constructorJson != null)
				array.add(constructorJson);
		}
		return array;
	}
}
