package pie.ilikepiefoo.kubejsoffline.util;

import org.junit.jupiter.api.Test;
import pie.ilikepiefoo.kubejsoffline.util.json.ClassJSONManager;

import java.lang.reflect.Method;

class SafeOperationsTest {

	public GenericsExample<String> example = new GenericsExample<>();
	public GenericNumberExample example2 = new GenericNumberExample();

	@Test
	void safeUnwrapReturnTypeName() {
		final String methodFormat = "Method '%s' has return type '%s' which maps to '%s'%n";
		final String parameterFormat = "Parameter '%s' maps to '%s'%n";
		for (final Method method : this.example2.getClass().getMethods()) {
			System.out.println("========================================");
			System.out.printf(methodFormat, method.getName(), method.getReturnType().getCanonicalName(), SafeOperations.safeUnwrapReturnTypeName(method));
			for (final var parameter : method.getParameters()) {
				System.out.printf(parameterFormat, parameter.getType().getName(), SafeOperations.safeUnwrapReturnTypeName(parameter));
			}
		}
		ClassJSONManager.getInstance().getTypeData(this.example.getClass());
		ClassJSONManager.getInstance().getTypeData(this.example2.getClass());
	}

	@Test
	void safeUniqueTypeName() {
	}

	@Test
	void safeUnwrapReturnType() {
	}

	@Test
	void safeUnwrapName() {
	}

}