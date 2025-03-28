package pie.ilikepiefoo.kubejsoffline.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import pie.ilikepiefoo.kubejsoffline.DocumentationThread;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;
import pie.ilikepiefoo.kubejsoffline.MinecraftDocumentationBridge;

public class DocumentCommand implements CommandRegistrationEvent {
    /**
     * This event is invoked after the server registers it's commands.
     * Equivalent to Forge's {@code RegisterCommandsEvent} and Fabric's {@code CommandRegistrationCallback}.
     *
     * @param dispatcher The command dispatcher to register commands to.
     * @param selection  The selection where the command can be executed.
     */
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(KubeJSOffline.MOD_ID)
                .requires((source) -> source.hasPermission(2))
                .executes((context) -> {
                    MinecraftDocumentationBridge bridge = new MinecraftDocumentationBridge(
                            (message) -> context.getSource().sendSuccess(message, true)
                    );
                    bridge.sendMessage(new TextComponent("KubeJS Offline has started... Please wait..."));
                    DocumentationThread thread = new DocumentationThread(bridge);
                    thread.start();
                    return 1;
                }));
    }
}
