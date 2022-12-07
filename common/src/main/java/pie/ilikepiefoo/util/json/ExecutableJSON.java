package pie.ilikepiefoo.util.json;

import com.google.gson.JsonObject;
import pie.ilikepiefoo.util.SafeOperations;

import javax.annotation.Nonnull;
import java.lang.reflect.Executable;

public class ExecutableJSON {
	public static void attach(@Nonnull JsonObject object, @Nonnull Executable executable) {
		// Parameters of the method
		var parameters = SafeOperations.tryGet(executable::getParameters);
		if(parameters.isEmpty()) // Throw out any methods that have broken parameters.
			return;

		var parametersJson = ParameterJSON.of(executable);
		if(parametersJson == null) // Throw out any methods that have broken parameters.
			return;

		// Attach Parameters
		object.add(JSONProperty.PARAMETERS.jsName, parametersJson);

		// Modifiers of the method
		object.addProperty(JSONProperty.MODIFIERS.jsName, executable.getModifiers());
	}
}
