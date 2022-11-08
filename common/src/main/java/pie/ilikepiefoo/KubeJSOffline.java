package pie.ilikepiefoo;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.Event;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.util.ReflectionHelper;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

public class KubeJSOffline {
    public static final String MOD_ID = "kubejsoffline";
	public static final String MOD_NAME = "KubeJS Offline";
	public static final Logger LOG = LogManager.getLogger();
	public static ReflectionHelper HELPER = null;


    public static void init() {
		EventHandler.init();
		CommandRegistryHandler.EVENT.register(new CommandRegistryHandler());
		LifecycleEvent.SETUP.register(KubeJSOffline::createBindingsMap);
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
						DocumentationThread thread = new DocumentationThread(FakeBindingsEvent.bindings);
						thread.setPrettyPrint(false);
						thread.start();
						return 1;
						// Add Boolean argument for pretty printing.
					})
					.then(Commands.literal("pretty")
							.executes((context) -> {
								context.getSource().sendSuccess(new TextComponent("KubeJS Offline has started... Please wait..."), false);
								DocumentationThread thread = new DocumentationThread(FakeBindingsEvent.bindings);
								thread.setPrettyPrint(true);
								thread.start();
								return 1;
							})
					));
		}
	}

	private static void createBindingsMap() {
		LOG.info("Creating Bindings Map...");
		for(ScriptType scriptType : ScriptType.values())
		{
			LOG.info("Creating Bindings Map for ScriptType: " + scriptType);
			FakeScriptManager manager = new FakeScriptManager(scriptType);
			FakeBindingsEvent event = new FakeBindingsEvent(manager);
			KubeJSPlugins.forEachPlugin(plugin -> plugin.addBindings(event));
			BindingsEvent.EVENT.invoker().accept(event);
			LOG.info("Bindings Map for ScriptType: " + scriptType + " has been created.");
		}
		LOG.info("Bindings Map has been created.");

		// Log size of bindings map.
		LOG.info("Bindings Map Size: " + FakeBindingsEvent.bindings.size());
	}

}