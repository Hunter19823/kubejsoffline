package pie.ilikepiefoo.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationJSON {
	public static JsonObject of(Annotation annotation) {
		JsonObject object = new JsonObject();
		var type = SafeOperations.safeUnwrapReturnTypeName(SafeOperations.tryGet(annotation::annotationType).orElse(null));
		if(type != null)
			object.addProperty("type", type);
		var description = SafeOperations.tryGet(annotation::toString);
		description.ifPresent(s -> object.addProperty("toString", s));
		return object;
	}

	public static JsonArray of(AnnotatedElement element) {
		if(element == null)
			return new JsonArray();
		JsonArray array = new JsonArray();
		var temp = SafeOperations.tryGet(element::getAnnotations);
		for(var annotation : temp.orElse(new Annotation[0]))
			array.add(of(annotation));
		return array;
	}
}
