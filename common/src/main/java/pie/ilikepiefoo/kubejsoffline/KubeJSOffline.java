package pie.ilikepiefoo.kubejsoffline;

import pie.ilikepiefoo.kubejsoffline.util.ReflectionHelper;

public class KubeJSOffline {
    public static final String MOD_ID = "kubejsoffline";
    public static final String MOD_NAME = "KubeJS Offline";
    public static ReflectionHelper HELPER = null;


    public static void init() {
        EventHandler.init();
    }


}