package pie.ilikepiefoo;

import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;
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

//		Thread thread = new DocumentationThread();
//		thread.setPriority(5);
//		thread.start();
		LifecycleEvent.SERVER_BEFORE_START.register(new ServerStarted());
	}

	public static class ServerStarted implements LifecycleEvent.ServerState {
		@Override
		public void stateChanged(MinecraftServer instance) {
			Thread thread = new DocumentationThread();
			thread.setPriority(5);
			thread.start();
		}
	}

}