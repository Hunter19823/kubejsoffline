package pie.ilikepiefoo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.html.page.ClassPage;
import pie.ilikepiefoo.html.page.IndexPage;
import pie.ilikepiefoo.util.ClassCluster;
import pie.ilikepiefoo.util.ClassFinder;
import pie.ilikepiefoo.util.ClassTree;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class DocumentationThread extends Thread {
	public static final Logger LOG = LogManager.getLogger();
	public static final ClassTree TREE = new ClassTree();
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
		LOG.info("Documentation Thread has started!");
		// Setup the ClassFinder
		ClassFinder.INSTANCE.addToSearch(KubeJSOffline.HELPER.getClasses());
		//ClassFinder.INSTANCE.onSearched(TREE::addClass);

		// Start the ClassFinder
		LOG.info("Documentation Thread has begun searching classes!");
		sendMessage(("Documentation Thread has begun searching through classes, expect possible lag..."));
		while(!ClassFinder.INSTANCE.isFinished()) {
			ClassFinder.INSTANCE.searchCurrentDepth();
		}


		LOG.info("Documentation Thread has finished searching classes!");
		sendMessage(("Documentation Thread has finished searching through classes. The class tree must be compressed to save memory, expect possible lag..."));

		// Compress the ClassTree
		//TREE.compress();
		sendMessage(("Documentation Thread has finished compressing the class tree."));

		sendMessage(("Documentation Thread is now dumping connections..."));
		// Dump connections.
		dumpConnections();

		sendMessage(("Documentation Thread is now dumping class jsons..."));
		// Dump ClassTree to JSON
		dumpJSONs();

		LOG.info("Documentation Thread has finished!");
		sendMessage(("Documentation Thread has finished generating all documents!"));
		sendMessage(("Now clearing memory..."));
		//TREE.clear();
		ClassFinder.INSTANCE.clear();
		sendMessage(("Memory cleared!"));
    }

	private void dumpConnections() {
		var out = RelationsJSON.of(ClassFinder.INSTANCE.getRelationships());

		writeFile("relationships.json","", out);
	}

	private void dumpJSONs() {
		ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().forEach((entry) -> {
			if(entry.getValue() == ClassFinder.SearchState.SEARCHED)
				dumpJSON(entry.getKey());
		});
		writeFile("fullClassTree.json","", ClassJSONManager.getInstance().getTypeData());
	}

	private void dumpJSON(Class<?> subject) {
		var url = ClassPage.getURL(subject);
		if(url.isEmpty()) {
			return;
		}
		ClassJSON.of(subject);
	}

	private void generateClassPages() {
		// Write Index File first.
		LOG.info("Documentation Thread is Creating Index Page...");
		var page = createIndexPage();
		LOG.info("Documentation Thread is Writing Index Page...");
		writeFile("index.html", "", page.toHTML());
		LOG.info("Documentation Thread has finished writing Index Page!");


		sendMessage(("Documentation Thread has begun writing files... This may take the longest depending on your system's write speed."));
		TREE.getExtensionRoot().stream().parallel().flatMap((cluster) -> cluster.getClasses().parallelStream()).distinct().forEachOrdered((target) -> {
			var url = ClassPage.getURL(target);
			if(url.isEmpty()) {
				return;
			}
			var file_name = url.substring(url.lastIndexOf('/')+1);
			var file_path = url.substring(1, url.lastIndexOf('/'));

			ClassCluster cluster = null;
			try {
				cluster = TREE.findExtensionCluster(target);
			} catch (Throwable e) {
				LOG.error("Error while finding extension cluster for {}.", target.getName(), e);
			}
			if(cluster == null || target == Object.class) {
				writeFile(file_name, file_path, new ClassPage(target, Collections.emptySet()).toHTML());
			}else {
				writeFile(file_name, file_path, new ClassPage(target, cluster.getClasses()).toHTML());
			}
		});
	}

	private static void sendMessage(String message) {
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent(message));
	}

	private IndexPage createIndexPage() {
		Class[] eventClasses = Arrays.stream(KubeJSOffline.HELPER.getEventClasses()).flatMap((subject)
				-> TREE.findExtensionCluster(subject).getClasses().stream()
		).distinct().sorted(Comparator.comparing(Class::getName)).toList().toArray(new Class[0]);


		return new IndexPage(bindings, eventClasses);
	}

	private static Path getOutputPath() {
		return Path.of("build/test/generated/html");
	}

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
}
