package pie.ilikepiefoo.kubejsoffline.data;

import javax.annotation.Nonnull;

public class MethodData extends CommonData {
	public MethodData(int modifiers, @Nonnull String name, @Nonnull ClassData returnType) {
		super(modifiers);
		setName(name);
		setType(returnType);
	}
}
