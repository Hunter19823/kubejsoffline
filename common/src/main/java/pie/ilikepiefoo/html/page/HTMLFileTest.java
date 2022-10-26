package pie.ilikepiefoo.html.page;


import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import pie.ilikepiefoo.html.tag.collection.TableTag;
import pie.ilikepiefoo.html.tag.text.HeaderTag;
import pie.ilikepiefoo.html.tag.text.ParagraphTag;
import pie.ilikepiefoo.html.util.TagWrapper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class HTMLFileTest {
	public static final Logger LOG = LogManager.getLogger();
	public Path getOutputPath() {
		return Path.of("build/test/generated/html");
	}
	public void writeFile(String fileName, String localPath, String content){
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

	public TableTag getExampleTable() {
		TableTag tag = new TableTag();
		var h1Tag = new HeaderTag(1).setContent("Test Header 3");
		tag.addHeader(TagWrapper.ofList("Test Header 1","Test Header 2",h1Tag));
		tag.addRow(TagWrapper.ofList("Test Entry 1", "Test Entry 2", "Test Entry 3"));
		tag.addRow(TagWrapper.ofList("Test Entry 4", "Test Entry 5", "Test Entry 6"));

		return tag;
	}

	@Test
	public void toHTML() {
		var test = new HTMLFile();
		test.BODY_TAG.add(new HeaderTag(5).setContent("Test Header Tag 5"));
		test.BODY_TAG.add(new HeaderTag(1).setContent("Test Header Tag 1"));
		test.BODY_TAG.add(new ParagraphTag().setContent("Test Paragraph\nThis is a new Paragraph\nCool Stuff right?"));
		test.BODY_TAG.add(getExampleTable());
		LOG.info(test);
		writeFile("index.html", "", test.toHTML());
	}
}