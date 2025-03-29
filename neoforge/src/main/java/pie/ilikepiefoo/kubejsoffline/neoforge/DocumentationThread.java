package pie.ilikepiefoo.kubejsoffline.neoforge;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import pie.ilikepiefoo.kubejsoffline.core.DocumentationGenerator;
import pie.ilikepiefoo.kubejsoffline.core.api.DocumentationBridge;
import pie.ilikepiefoo.kubejsoffline.core.html.tag.Tag;
import pie.ilikepiefoo.kubejsoffline.core.util.json.BindingsJSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class DocumentationThread extends Thread {
    public static final Logger LOG = LogManager.getLogger();

    private final DocumentationBridge bridge;

    public DocumentationThread(DocumentationBridge bridge) {
        super("KJSOffline DocThread");
        this.bridge = bridge;

    }

    @Nullable
    private static File writeHTMLPage(final Tag<?> content) {
        final File output = getFile();

        try (final Writer writer = new FileWriter(output, StandardCharsets.UTF_8)) {
            content.writeHTML(writer);
            writer.flush();
        } catch (final IOException e) {
            LOG.error("Failed to write file: index.html to {}", output.getPath(), e);
            return null;
        }
        return output;
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
        return pie.ilikepiefoo.kubejsoffline.neoforge.KubeJSOffline.WORKING_DIR.resolve("kubejs/documentation");
    }

    @Override
    public void run() {
        // Wait for Helper to be initialized.
        LOG.info("Starting Documentation Thread...");
        while (null == pie.ilikepiefoo.kubejsoffline.neoforge.KubeJSOffline.HELPER) {
            LOG.info("Documentation Thread idling until Helper becomes available...");
            try {
                this.wait(5000);
            } catch (final InterruptedException e) {
                LOG.error(e);
            }
        }
        LOG.info("Helper is available, now finding bindings...");
        JsonObject bindings = new JsonObject();
//        for (Map.Entry<ScriptType, JsonObject> entry : FakeBindingsEvent.bindingsJSON.entrySet()) {
//            bindings.add(entry.getKey().name(), entry.getValue());
//        }
        BindingsJSON.setJsonObject(bindings);
        DocumentationGenerator.generateHtmlPage(pie.ilikepiefoo.kubejsoffline.neoforge.KubeJSOffline.HELPER, bridge, new pie.ilikepiefoo.kubejsoffline.neoforge.RhinoTypeMapper(), getFile());
    }
}
