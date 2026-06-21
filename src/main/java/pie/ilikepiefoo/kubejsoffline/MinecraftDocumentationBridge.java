package pie.ilikepiefoo.kubejsoffline;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import pie.ilikepiefoo.kubejsoffline.core.api.DocumentationBridge;
import pie.ilikepiefoo.kubejsoffline.util.ComponentUtils;

import java.util.function.Consumer;

public class MinecraftDocumentationBridge implements DocumentationBridge {

    public Consumer<Component> sendMessage;

    public MinecraftDocumentationBridge(Consumer<Component> sendMessage) {
        this.sendMessage = sendMessage;
    }

    public void sendMessage(final String message) {
        this.sendMessage(ComponentUtils.create(message));
    }

    public void sendMessage(final Component message) {
        this.sendMessage.accept(message);
    }

    public void sendMessageWithLink(final String message, final String linkText, final String link) {
        this.sendMessage(ComponentUtils.create(message).append(ComponentUtils.create(linkText).withStyle((style) -> {
            return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, link)).withUnderlined(true).withColor(ChatFormatting.AQUA);
        })));
    }
}
