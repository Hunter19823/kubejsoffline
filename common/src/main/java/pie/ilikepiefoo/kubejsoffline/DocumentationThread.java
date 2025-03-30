package pie.ilikepiefoo.kubejsoffline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.core.api.DocumentationBridge;
import pie.ilikepiefoo.kubejsoffline.core.api.DocumentationProvider;
import pie.ilikepiefoo.kubejsoffline.core.impl.SimpleDocumentationProvider;

import java.io.File;
import java.nio.file.Path;

public class DocumentationThread extends Thread {
    public static final Logger LOG = LogManager.getLogger();

    private final DocumentationBridge bridge;

    public DocumentationThread(DocumentationBridge bridge) {
        super("KJSOffline DocThread");
        this.bridge = bridge;

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File getFile() {
        final Path outputPath = getOutputPath().toAbsolutePath();
        if (!outputPath.toFile().exists()) {
            outputPath.toFile().mkdirs();
        }

        return outputPath.resolve("index.html").toFile();
    }

    private static Path getOutputPath() {
        return KubeJSOffline.WORKING_DIR.resolve("kubejs/documentation");
    }

    @Override
    public void run() {
        // Wait for Helper to be initialized.
        LOG.info("Starting Documentation Thread...");
        while (null == KubeJSOffline.HELPER) {
            LOG.info("Documentation Thread idling until Helper becomes available...");
            try {
                this.wait(5000);
            } catch (final InterruptedException e) {
                LOG.error(e);
            }
        }
        LOG.info("Helper is available, now finding bindings...");
        DocumentationProvider provider = new SimpleDocumentationProvider.Builder()
                .setReflectionHelper(KubeJSOffline.HELPER)
                .setDocumentationBridge(bridge)
                .setTypeNameMapper(new RhinoTypeMapper())
                .setBindingsProvider(new FakeBindingsEvent())
                .build();
        provider.generateDocumentation(getFile());
    }
}
