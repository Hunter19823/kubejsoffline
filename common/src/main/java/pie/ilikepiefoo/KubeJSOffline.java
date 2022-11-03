package pie.ilikepiefoo;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.latvian.mods.kubejs.script.ScriptManager;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptType;
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
	private static Map<String, Map<Class<?>,Set<ScriptType>>> bindings;


    public static void init() {
		EventHandler.init();
		bindings = createBindingsMap();
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
						DocumentationThread thread = new DocumentationThread(bindings);
						thread.setPrettyPrint(false);
						thread.start();
						return 1;
						// Add Boolean argument for pretty printing.
					})
					.then(Commands.literal("pretty")
							.executes((context) -> {
								context.getSource().sendSuccess(new TextComponent("KubeJS Offline has started... Please wait..."), false);
								DocumentationThread thread = new DocumentationThread(bindings);
								thread.setPrettyPrint(true);
								thread.start();
								return 1;
							})
					));
		}
	}

	private static Map<String, Map<Class<?>,Set<ScriptType>>> createBindingsMap() {
		Map<String, Map<Class<?>,Set<ScriptType>>> bindings = new TreeMap<>();
		for(ScriptManager manager : getScriptManagers()) {
			try {
				for(ScriptPack pack : manager.packs.values()) {
					for(Object id : pack.scope.getAllIds()) {
						if(id == null)
							continue;
						if(id instanceof String bindingName) {
							var unWrapped = unwrapObject(pack.scope.get(bindingName, pack.scope));
							if(!bindings.containsKey(bindingName)) {
								bindings.put(bindingName, new HashMap<>());
								var map = bindings.get(bindingName);
								map.put(unWrapped, EnumSet.noneOf(ScriptType.class));
								map.get(unWrapped).add(manager.type);
							}else {
								bindings.get(bindingName).get(unWrapped).add(manager.type);
							}
						}
					}
				}
			}catch (Throwable e) {
				LOG.error("Error while processing bindings for script manager {} for script type {}.", manager, manager.type, e);
			}
		}
		return bindings;
	}

	private static Class unwrapObject(Object object) {
		while(object instanceof Wrapper javaObject) {
			object = javaObject.unwrap();
		}

		if(object instanceof Class<?> clazz) {
			return clazz;
		}
		return object.getClass();
	}

	private static ScriptManager[] getScriptManagers() {
		ScriptManager[] managers = new ScriptManager[ScriptType.values().length];
		for(ScriptType type : ScriptType.values()) {
			try {
				managers[type.ordinal()] = type.manager.get();
			} catch (Exception e) {
				LOG.error("Error while getting script manager for {}.", type, e);
			}
		}
		// Remove nulls
		return Arrays.stream(managers).filter(Objects::nonNull).toArray(ScriptManager[]::new);
	}

}