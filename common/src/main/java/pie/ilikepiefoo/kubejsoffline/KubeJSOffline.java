package pie.ilikepiefoo.kubejsoffline;


import pie.ilikepiefoo.kubejsoffline.core.api.ReflectionHelper;

import java.nio.file.Path;

public class KubeJSOffline {
    public static final String MOD_ID = "kubejsoffline";
    public static final String MOD_NAME = "KubeJS Offline";
    public static ReflectionHelper HELPER = null;
    public static Path WORKING_DIR = null;


    public static void init(Path workingDir) {
        WORKING_DIR = workingDir;
        EventHandler.init();
    }


}