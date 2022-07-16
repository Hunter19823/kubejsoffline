package pie.ilikepiefoo.fabric;

import pie.ilikepiefoo.KubeJSOffline;
import net.fabricmc.api.ModInitializer;

public class KubeJSOfflineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		KubeJSOffline.HELPER = new ReflectionHelperFabric();
        KubeJSOffline.init();
    }
}
