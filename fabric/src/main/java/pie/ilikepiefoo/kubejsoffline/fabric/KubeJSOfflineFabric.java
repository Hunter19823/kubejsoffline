package pie.ilikepiefoo.kubejsoffline.fabric;

import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;
import net.fabricmc.api.ModInitializer;

public class KubeJSOfflineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
		KubeJSOffline.HELPER = new ReflectionHelperFabric();
        KubeJSOffline.init();
    }
}
