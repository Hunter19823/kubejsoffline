package pie.ilikepiefoo.kubejsoffline.html.tag;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.KubeJSOffline;
import pie.ilikepiefoo.kubejsoffline.util.DocumentationBridge;

import java.io.InputStream;
import java.io.Writer;

public class CustomAssetTag extends CustomTag {
    public static final Logger LOG = LogManager.getLogger();
    private final ResourceLocation file;
    private final DocumentationBridge documentationBridge;

    public CustomAssetTag(String tagName, String contentLocation, DocumentationBridge documentationBridge) {
        super(tagName, true);
        file = new ResourceLocation(KubeJSOffline.MOD_ID, contentLocation);
        this.documentationBridge = documentationBridge;
    }

    @Override
    public void writeContent(Writer writer) {
        if (!documentationBridge.hasResource(file)) {
            LOG.error("Could not find {} tag from file: {}\n THIS WILL CAUSE SEVERE PROBLEMS WITH THE RESULTING DOCUMENTATION FILE!", this.name, file.toDebugFileName());
            return;
        }
        try (InputStream stream = documentationBridge.getResource(file)) {
            // Read the file and write it to the writer.
            int c;
            while ((c = stream.read()) != -1) {
                writer.write(c);
            }
            writer.flush();
        } catch (Exception e) {
            LOG.error("Error writing {} tag from file: {}", this.name, file.toDebugFileName(), e);
        }
    }
}
