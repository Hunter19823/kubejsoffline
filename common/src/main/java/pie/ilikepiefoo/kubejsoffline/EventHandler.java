package pie.ilikepiefoo.kubejsoffline;


import dev.architectury.event.events.common.LifecycleEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.command.DocumentCommand;

public class EventHandler {
	public static final Logger LOG = LogManager.getLogger();

	public static void init() {
		DocumentCommand.EVENT.register(new DocumentCommand());
		LifecycleEvent.SETUP.register(EventHandler::createBindingsMap);
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
	}
}
