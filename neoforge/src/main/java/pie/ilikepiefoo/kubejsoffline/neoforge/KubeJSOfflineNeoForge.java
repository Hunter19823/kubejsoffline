package pie.ilikepiefoo.kubejsoffline.neoforge;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

@Mod(KubeJSOffline.MOD_ID)
public class KubeJSOfflineNeoForge extends KubeJSOffline {
    public KubeJSOfflineNeoForge() {
        // Submit our event bus to let architectury register our content on the right time
        KubeJSOffline.HELPER = new ReflectionHelperNeoForge();
        KubeJSOffline.init(FMLPaths.GAMEDIR.get());
    }

}
