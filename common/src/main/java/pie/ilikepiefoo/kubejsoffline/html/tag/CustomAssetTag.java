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
	private final DocumentationBridge bridge;

	public CustomAssetTag(String tagName, String contentLocation, DocumentationBridge bridge) {
		super(tagName, true);
		file = new ResourceLocation(KubeJSOffline.MOD_ID, contentLocation);
		this.bridge = bridge;
	}

	@Override
	public void writeContent(Writer writer) {
		if (this.bridge.hasResource(file)) {
			LOG.error("Could not find {} tag from file: {}\n THIS WILL CAUSE SEVERE PROBLEMS WITH THE RESULTING DOCUMENTATION FILE!", this.name, file.toDebugFileName());
			return;
		}
		try (InputStream stream = this.bridge.getResource(file)) {
			// Read the file and write it to the writer.
			int c;
			while ((c = stream.read()) != -1) {
				writer.write(c);
			}
			writer.flush();
		} catch (Exception e) {
			LOG.error("Error writing " + this.name + " tag from file: " + file.toDebugFileName(), e);
		}
	}
}
