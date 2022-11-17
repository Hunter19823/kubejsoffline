package pie.ilikepiefoo.html.tag;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

public class LocalScriptTag extends CustomTag {
	public static final Logger LOG = LogManager.getLogger();
	private final File file;
	public LocalScriptTag(String scriptLocation) {
		super("script", true);
		file = new File(scriptLocation);
	}

	@Override
	public void writeContent(Writer writer) throws IOException {
		if(file.exists()) {
			try (FileReader reader = new FileReader(file)){
				// Read the file and write it to the writer.
				int c;
				while ((c = reader.read()) != -1) {
					writer.write(c);
				}
				writer.flush();
			} catch (IOException e) {
				LOG.error(e);
			}
		}else{
			LOG.error("Script file does not exist: " + file.getAbsolutePath());
		}
	}
}
