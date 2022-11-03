package pie.ilikepiefoo.util.json;

import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import java.lang.reflect.Executable;

public class ExecutableJSON {
	public static void attach(JsonObject object, Executable executable) {
		// Parameters of the method
		var parameters = SafeOperations.tryGet(executable::getParameters);
		if(parameters.isPresent())
			object.add("param", ParameterJSON.of(executable));

		// Annotations of the method
		var annotations = AnnotationJSON.of(executable);
		if(annotations.size() > 0)
			object.add("ano", annotations);

		// Modifiers of the method
		var modifiers = ModifierJSON.of(executable.getModifiers());
		if(modifiers.size() > 0)
			object.add("mod", modifiers);
	}
}
