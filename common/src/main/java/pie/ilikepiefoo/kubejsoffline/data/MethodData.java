package pie.ilikepiefoo.kubejsoffline.data;

import javax.annotation.Nonnull;

public class MethodData extends CommonData {
	public MethodData( int modifiers, @Nonnull String name, @Nonnull TypeData returnType ) {
		super(name, modifiers);
		setType(returnType);
	}
}
