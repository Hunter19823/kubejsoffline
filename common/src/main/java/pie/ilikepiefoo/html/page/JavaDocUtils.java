package pie.ilikepiefoo.html.page;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class JavaDocUtils {
	public static final Logger LOG = LogManager.getLogger();


	public static void main(String[] args) {
		//new DocumentationThread().run();
		MainPage page = new MainPage();
		writeFile("index.html", "", page.toHTML());
	}

	private static Path getOutputPath() {
		return Path.of("build/test/generated/html");
	}

	private static void writeFile(String fileName, String localPath, String content) {
		Path outputPath = getOutputPath().resolve(localPath).toAbsolutePath();
		outputPath.toFile().mkdirs();
		LOG.info("Writing file: "+fileName+" to "+outputPath);
		File output = outputPath.resolve(fileName).toFile();
		LOG.info("Link to File: "+output.toPath().toAbsolutePath());

		try {
			FileUtils.write(output, content, StandardCharsets.UTF_16, false);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
}
