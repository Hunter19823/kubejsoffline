package pie.ilikepiefoo.kubejsoffline.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import pie.ilikepiefoo.kubejsoffline.DocumentationThread;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;
import pie.ilikepiefoo.kubejsoffline.MinecraftDocumentationBridge;
import pie.ilikepiefoo.kubejsoffline.util.ComponentUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class DocumentCommand implements CommandRegistrationEvent {
    /**
     * This event is invoked after the server registers it's commands.
     * Equivalent to Forge's {@code RegisterCommandsEvent} and Fabric's {@code CommandRegistrationCallback}.
     *
     * @param dispatcher The command dispatcher to register commands to.
     * @param selection  The selection where the command can be executed.
     */
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal(KubeJSOffline.MOD_ID)
                .requires((source) -> source.hasPermission(2))
                .executes((context) -> {
                    MinecraftDocumentationBridge bridge = new MinecraftDocumentationBridge(
                            (message) -> context.getSource().sendSuccess(() -> message, true)
                    );
                    bridge.sendMessage(ComponentUtils.create("KubeJS Offline has started... Please wait..."));
                    DocumentationThread thread = new DocumentationThread(bridge);
                    var future = CompletableFuture.runAsync(thread);
                    future.exceptionallyAsync((throwable) -> {
                        bridge.sendMessage(ComponentUtils.create("An error occurred while generating KubeJS documentation. Please report to @pietheniceguy on discord."));
                        bridge.sendMessage(ComponentUtils.create("Error: " + throwable.getMessage()));
                        KubeJSOffline.LOG.error("Error while generating KubeJS documentation", throwable);
                        if (KubeJSOffline.isAutomaticGenerationEnabled()) {
                            KubeJSOffline.LOG.error("Killing the JVM due to error in documentation generation.");
                            Minecraft.getInstance().close();
                            System.exit(1);
                        }
                        return null;
                    }).thenAccept((v) -> {
                        if (KubeJSOffline.isAutomaticGenerationEnabled()) {
                            KubeJSOffline.LOG.info("Killing the JVM after documentation generation.");
                            System.exit(0);
                        } else {
                            KubeJSOffline.LOG.info("Documentation Thread finished successfully.");
                        }
                    });
                    if (KubeJSOffline.isAutomaticGenerationEnabled()) {
                        // If automatic generation is enabled, we want to time out the future after 5 minutes.
                        // This is to prevent the server from hanging indefinitely if something goes wrong.
                        KubeJSOffline.LOG.info("Setting timeout for documentation generation to 5 minutes.");
                        future.orTimeout(5, TimeUnit.MINUTES);
                    }
                    return 1;
                }));
    }
}
