package pie.ilikepiefoo.kubejsoffline.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;

@Mod(KubeJSOffline.MOD_ID)
public class KubeJSOfflineForge {
    public KubeJSOfflineForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(KubeJSOffline.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        KubeJSOffline.HELPER = new ReflectionHelperForge();
        KubeJSOffline.init();
    }
}
