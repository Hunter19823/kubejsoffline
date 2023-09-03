package pie.ilikepiefoo.kubejsoffline.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import pie.ilikepiefoo.kubejsoffline.util.SafeOperations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationJSON {
	@Nullable
	public static JsonObject of(@Nullable Annotation annotation) {
		JsonObject object = new JsonObject();
		var annotationType = SafeOperations.tryGet(annotation::annotationType);
		if(annotationType.isEmpty())
			return null;
		var type = ClassJSONManager.getInstance().getTypeData(annotationType.get());
		if(type != null)
			object.addProperty(JSONProperty.ANNOTATION_TYPE.jsName, type.get(JSONProperty.TYPE_ID.jsName).getAsInt());

		var description = SafeOperations.tryGet(annotation::toString);
		if (description.isEmpty()) {
			return null;
		}
		description.ifPresent(s -> {
			var trimmed = trim(s);
			if (!trimmed.isEmpty()) {
				object.addProperty(JSONProperty.ANNOTATION_STRING.jsName, trimmed);
			}
		});

		return object;
	}

	/**
	 * In order to reduce file size, all annotations will have @fullClassName(...) trimmed to just the contents within the parenthesis.
	 *
	 * @param s the string to trim.
	 * @return the trimmed string.
	 */
	public static String trim(@Nonnull String s) {
		return s.substring(s.indexOf('(') + 1, s.lastIndexOf(')'));
	}

	@Nonnull
	public static JsonArray of(@Nullable AnnotatedElement element) {
		if(element == null)
			return new JsonArray();
		JsonArray array = new JsonArray();
		var temp = SafeOperations.tryGet(element::getAnnotations);
		for(var annotation : temp.orElse(new Annotation[0])) {
			var annotationJson = of(annotation);
			if(annotationJson != null)
				array.add(annotationJson);
		}
		return array;
	}
}
