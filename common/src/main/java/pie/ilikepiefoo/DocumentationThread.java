package pie.ilikepiefoo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.html.page.IndexPage;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.util.ClassFinder;
import pie.ilikepiefoo.util.json.ClassJSON;
import pie.ilikepiefoo.util.json.ClassJSONManager;
import pie.ilikepiefoo.util.json.RelationsJSON;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class DocumentationThread extends Thread {
	public static final Logger LOG = LogManager.getLogger();
	public final Map<String, Map<Class<?>,Set<ScriptType>>> bindings;

	private static Gson GSON = new GsonBuilder().create();

	public DocumentationThread(Map<String, Map<Class<?>,Set<ScriptType>>> bindings) {
		this.bindings = bindings;
	}

	public void setPrettyPrint(boolean prettyPrint) {
		if (prettyPrint) {
			GSON = new GsonBuilder().setPrettyPrinting().create();
		} else {
			GSON = new GsonBuilder().create();
		}
	}

    @Override
    public void run() {
		// Wait for Helper to be initialized.
		LOG.info("Starting Documentation Thread...");
		while (KubeJSOffline.HELPER == null){
			LOG.info("Documentation Thread idling until Helper becomes available...");
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				LOG.error(e);
			}
		}
		// Log the bindings.
		LOG.info("Bindings: " + GSON.toJson(bindings));
		int step = 0;
		int totalSteps = 9;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Initializing ClassFinder and Reflections Library...", ++step, totalSteps));
		long start = System.currentTimeMillis();
		long timeMillis = System.currentTimeMillis();
		// Setup the ClassFinder
		ClassFinder.INSTANCE.addToSearch(KubeJSOffline.HELPER.getClasses());

		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] ClassFinder setup in %,dms", ++step, totalSteps, timeMillis));

		// Start the ClassFinder
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Starting ClassFinder...", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		while(!ClassFinder.INSTANCE.isFinished()) {
			ClassFinder.INSTANCE.searchCurrentDepth();
		}
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] ClassFinder finished in %,dms", step, totalSteps, timeMillis));

		// Dump connections.
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating JSON with class dependencies...", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		dumpConnections();
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] JSON with class dependencies generated in %,dms", step, totalSteps, timeMillis));


		// Dump ClassTree to JSON
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating JSON with full list of class data..", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		dumpJSONs();
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] JSON with full list of class data generated in %,dms", step, totalSteps, timeMillis));

		// Create index.html
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating index.html...", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		dumpIndex();
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html generated in %,dms", step, totalSteps, timeMillis));


		int totalClassSize = ClassFinder.INSTANCE.CLASS_SEARCH.size();
		int totalRelationSize = ClassFinder.INSTANCE.RELATIONSHIPS.size();
		// Clear and de-reference any data that is no longer needed.
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Clearing and de-referencing data...", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		ClassFinder.INSTANCE.clear();
		ClassJSONManager.getInstance().clear();
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Data cleared and dereferenced in %,dms", step, totalSteps, timeMillis));
		long end = System.currentTimeMillis();
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Documentation Thread finished in %,dms", ++step, totalSteps, end - start));
		sendMessage(String.format("[KJS Offline] [Step %d/%d] %,d classes found, %,d relationships found", ++step, totalSteps, totalClassSize, totalRelationSize));
    }

	private void dumpIndex() {
		IndexPage page = new IndexPage(bindings, GSON);
		writeFile("index.html", "", page);
	}

	private void dumpConnections() {
		var out = RelationsJSON.of(ClassFinder.INSTANCE.getRelationships());

		writeFile("relationships.json","", out);
	}

	private void dumpJSONs() {
		ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().forEach((entry) -> {
			if(entry.getValue() == ClassFinder.SearchState.SEARCHED)
				ClassJSON.of(entry.getKey());
		});
		writeFile("fullClassTree.json","", ClassJSONManager.getInstance().getTypeData());
	}

	private static void sendMessage(String message) {
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message));
	}

	private static Path getOutputPath() {
		return Path.of("build/test/generated/html");
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static File getFile(String fileName, String localPath) {
		Path outputPath = getOutputPath().resolve(localPath).toAbsolutePath();
		if(!outputPath.toFile().exists())
			outputPath.toFile().mkdirs();

		return outputPath.resolve(fileName).toFile();
	}

	private static void writeFile(String fileName, String localPath, String content) {
		File output = getFile(fileName, localPath);

		try (FileOutputStream fos = new FileOutputStream(output)) {
			fos.write(content.getBytes(StandardCharsets.UTF_16));
			//LOG.info("Successfully written file: " + fileName + " to " + outputPath);
		} catch (IOException e) {
			LOG.error("Failed to write file: " + fileName + " to " + output.getPath(), e);
		}
	}

	private static void writeFile(String fileName, String localPath, JsonElement content) {
		File output = getFile(fileName, localPath);

		try (Writer writer = new FileWriter(output)) {
			GSON.toJson(content, writer);
		} catch (IOException e) {
			LOG.error("Failed to write file: " + fileName + " to " + output.getPath(), e);
		}
	}

	private static void writeFile(String fileName, String localPath, Tag<?> content) {
		File output = getFile(fileName, localPath);

		try (Writer writer = new FileWriter(output)) {
			content.writeHTML(writer);
			writer.flush();
		} catch (IOException e) {
			LOG.error("Failed to write file: " + fileName + " to " + output.getPath(), e);
		}
	}
}
