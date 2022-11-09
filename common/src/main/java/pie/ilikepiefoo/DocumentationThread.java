package pie.ilikepiefoo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import pie.ilikepiefoo.html.page.IndexPage;
import pie.ilikepiefoo.html.tag.Tag;
import pie.ilikepiefoo.util.ClassFinder;
import pie.ilikepiefoo.util.json.ClassJSON;
import pie.ilikepiefoo.util.json.ClassJSONManager;
import pie.ilikepiefoo.util.json.RelationsJSON;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;

public class DocumentationThread extends Thread {
	public static final Logger LOG = LogManager.getLogger();

	private static final Gson GSON = new GsonBuilder().create();
	private String outputFile;

	public DocumentationThread() {
		super("KJSOffline DocThread");
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
		jsonifyConnections();
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] JSON with class dependencies generated in %,dms", step, totalSteps, timeMillis));


		// Dump ClassTree to JSON
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating JSON with full list of class data..", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		jsonifyClasses();
		timeMillis = System.currentTimeMillis() - timeMillis;
		sendMessage(String.format("[KJS Offline] [Step %d/%d] JSON with full list of class data generated in %,dms", step, totalSteps, timeMillis));

		// Create index.html
		sendMessage(String.format("[KJS Offline] [Step %d/%d] Generating index.html...", ++step, totalSteps));
		timeMillis = System.currentTimeMillis();
		var output = createIndexPage();
		timeMillis = System.currentTimeMillis() - timeMillis;
		if(output != null)
			sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html generated in %,dms", step, totalSteps, timeMillis));
		else
			sendMessage(String.format("[KJS Offline] [Step %d/%d] index.html failed to generate after %,dms!", step, totalSteps, timeMillis));


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
		if(output != null)
			sendLink(String.format("[KJS Offline] [Step %d/%d] The Documentation page can be found at kubejs/documentation/index.html or by clicking ", step, totalSteps),"here", "kubejs/documentation/index.html");
    }

	@Nullable
	private File createIndexPage() {
		IndexPage page = new IndexPage(GSON);
		return writeHTMLPage(page);
	}

	private void jsonifyConnections() {
		RelationsJSON.of(ClassFinder.INSTANCE.getRelationships());
	}

	private void jsonifyClasses() {
		ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().forEach((entry) -> {
			if(entry.getValue() == ClassFinder.SearchState.SEARCHED)
				ClassJSON.of(entry.getKey());
		});
	}

	private static void sendMessage(String message) {
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message));
	}

	private static void sendLink(String message, String linkText, String link) {
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message).append(new TextComponent(linkText).withStyle((style) -> {
			return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, link)).withUnderlined(true).withColor(ChatFormatting.AQUA);
		})));
	}

	private static Path getOutputPath() {
		return KubeJSOffline.HELPER.getWorkingDirectory().resolve("kubejs/documentation");
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	private static File getFile() {
		Path outputPath = getOutputPath().toAbsolutePath();
		if(!outputPath.toFile().exists())
			outputPath.toFile().mkdirs();

		return outputPath.resolve("index.html").toFile();
	}

	@Nullable
	private static File writeHTMLPage(Tag<?> content) {
		File output = getFile();

		try (Writer writer = new FileWriter(output)) {
			content.writeHTML(writer);
			writer.flush();
		} catch (IOException e) {
			LOG.error("Failed to write file: " + "index.html" + " to " + output.getPath(), e);
			return null;
		}
		return output;
	}
}
