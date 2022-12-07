package pie.ilikepiefoo.html.tag;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.KubeJSOffline;

import java.io.InputStream;
import java.io.Writer;

public class CustomAssetTag extends CustomTag {
	public static final Logger LOG = LogManager.getLogger();
	private final ResourceLocation file;
	public CustomAssetTag(String tagName, String contentLocation) {
		super(tagName, true);
		file = new ResourceLocation(KubeJSOffline.MOD_ID, contentLocation);
	}

	@Override
	public void writeContent(Writer writer) {
		try (InputStream stream = Minecraft.getInstance().getResourceManager().getResource(file).getInputStream()) {
			// Read the file and write it to the writer.
			int c;
			while ((c = stream.read()) != -1) {
				writer.write(c);
			}
			writer.flush();
		}catch (Exception e){
			LOG.error("Error writing "+this.name+" tag from file: " + file.toDebugFileName(), e);
		}
	}
}
