package pie.ilikepiefoo.kubejsoffline.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;

public class KubeJSOfflineFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        KubeJSOffline.HELPER = new ReflectionHelperFabric();
        KubeJSOffline.init(FabricLoader.getInstance().getGameDir());
    }
}
