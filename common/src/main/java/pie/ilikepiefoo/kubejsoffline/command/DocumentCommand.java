package pie.ilikepiefoo.kubejsoffline.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import pie.ilikepiefoo.kubejsoffline.DocumentationThread;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;

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
                    context.getSource().sendSuccess(new TextComponent("KubeJS Offline has started... Please wait..."), false);
                    DocumentationThread thread = new DocumentationThread();
                    thread.start();
                    return 1;
                }));
    }
}
