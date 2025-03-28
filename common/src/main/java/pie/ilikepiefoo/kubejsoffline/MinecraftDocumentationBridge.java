package pie.ilikepiefoo.kubejsoffline;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import pie.ilikepiefoo.kubejsoffline.core.api.DocumentationBridge;

import java.util.function.Consumer;

public class MinecraftDocumentationBridge implements DocumentationBridge {

    public Consumer<Component> sendMessage;

    public MinecraftDocumentationBridge(Consumer<Component> sendMessage) {
        this.sendMessage = sendMessage;
    }

    public void sendMessage(final String message) {
        this.sendMessage(new TextComponent(message));
    }

    public void sendMessage(final Component message) {
        this.sendMessage.accept(message);
    }

    public void sendMessageWithLink(final String message, final String linkText, final String link) {
        this.sendMessage(new TextComponent(message).append(new TextComponent(linkText).withStyle((style) -> {
            return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, link)).withUnderlined(true).withColor(ChatFormatting.AQUA);
        })));
    }
}
