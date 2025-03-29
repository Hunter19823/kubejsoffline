package pie.ilikepiefoo.kubejsoffline.neoforge;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;

public class ComponentUtils {

    public static MutableComponent create(String text) {
        return MutableComponent.create(new LiteralContents(text));
    }

}
