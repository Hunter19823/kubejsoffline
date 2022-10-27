package pie.ilikepiefoo;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.util.ReflectionHelper;

public class KubeJSOffline {
    public static final String MOD_ID = "kubejsoffline";
	public static final String MOD_NAME = "KubeJS Offline";
	public static final Logger LOG = LogManager.getLogger();
	public static ReflectionHelper HELPER = null;


    public static void init() {
		EventHandler.init();

		CommandRegistryHandler.EVENT.register(new CommandRegistryHandler());
	}


	public static class CommandRegistryHandler implements CommandRegistrationEvent {

		/**
		 * This event is invoked after the server registers it's commands.
		 * Equivalent to Forge's {@code RegisterCommandsEvent} and Fabric's {@code CommandRegistrationCallback}.
		 *
		 * @param dispatcher The command dispatcher to register commands to.
		 * @param selection  The selection where the command can be executed.
		 */
		@Override
		public void register(CommandDispatcher<CommandSourceStack> dispatcher, Commands.CommandSelection selection) {
			dispatcher.register(Commands.literal("kubejsoffline")
					.requires((source) -> source.hasPermission(2))
					.executes((context) -> {
						context.getSource().sendSuccess(new TextComponent("KubeJS Offline has started... Please wait..."), false);
						Thread thread = new DocumentationThread();
						thread.start();
						return 1;
					}));
		}
	}

}