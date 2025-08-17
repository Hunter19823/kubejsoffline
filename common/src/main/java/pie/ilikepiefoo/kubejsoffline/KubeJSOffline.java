package pie.ilikepiefoo.kubejsoffline;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.core.api.ReflectionHelper;

import java.nio.file.Path;

public class KubeJSOffline {
    public static final String MOD_ID = "kubejsoffline";
    public static final String MOD_NAME = "KubeJS Offline";
    public static ReflectionHelper HELPER = null;
    public static Path WORKING_DIR = null;
    public static final Logger LOG = LogManager.getLogger();


    public static void init(Path workingDir) {
        WORKING_DIR = workingDir;
        EventHandler.init();
    }


    public static boolean isAutomaticGenerationEnabled() {
        return System.getProperty("KUBEJS_OFFLINE_AUTO_GEN") != null || System.getenv("KUBEJS_OFFLINE_AUTO_GEN") != null;
    }


}