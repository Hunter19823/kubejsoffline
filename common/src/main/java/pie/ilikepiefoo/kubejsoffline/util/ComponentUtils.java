package pie.ilikepiefoo.kubejsoffline.util;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;

public class ComponentUtils {

	public static MutableComponent create(String text) {
		return MutableComponent.create(new LiteralContents(text));
	}
}
