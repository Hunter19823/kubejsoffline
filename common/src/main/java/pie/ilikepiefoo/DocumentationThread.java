package pie.ilikepiefoo;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.ScriptPack;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.NativeJavaObject;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.html.page.ClassPage;
import pie.ilikepiefoo.html.page.ClassTreePage;
import pie.ilikepiefoo.html.page.IndexPage;
import pie.ilikepiefoo.html.page.MainPage;
import pie.ilikepiefoo.util.ClassCluster;
import pie.ilikepiefoo.util.ClassFinder;
import pie.ilikepiefoo.util.ClassTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentationThread extends Thread {
	public static final Logger LOG = LogManager.getLogger();
	public static final ClassTree TREE = new ClassTree();

    @Override
    public void run() {
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
		ClassFinder.INSTANCE.addToSearch(KubeJSOffline.HELPER.getClasses());
		ClassFinder.INSTANCE.onSearched(TREE::addClass);


		LOG.info("Documentation Thread has begun searching classes!");
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Documentation Thread has begun searching through classes, expect possible lag..."));
		while(!ClassFinder.INSTANCE.isFinished()) {
			LOG.info("Class Count {}", ClassFinder.INSTANCE.CLASS_SEARCH.size());
			//ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().filter(entry->entry.getValue() == ClassFinder.SearchState.IN_QUEUE).forEach((entry)->ClassFinder.LOG.info("Queued Class:\t{}",entry.getKey()));
			ClassFinder.INSTANCE.searchCurrentDepth();
		}
		LOG.info("Documentation Thread has finished searching classes!");
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Documentation Thread has finished searching through classes. The class tree must be compressed to save memory, expect possible lag..."));
		LOG.info("Documentation Thread has begun memory compression!");
		TREE.compress();
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Documentation Thread has finished compressing the class tree."));
		LOG.info("Documentation Thread has finished memory compression!");
		//MainPage mainPage = new MainPage();
		//mainPage.SIDE_NAV.add(mainPage.addCluster(TREE.getFileRoot(),"FileRoot"));
		//mainPage.SIDE_NAV.add(mainPage.addCluster(TREE.getExtensionRoot(),"ExtensionRoot"));


		LOG.info("Documentation Thread has begun writing files!");

		// Write Index File first.
		LOG.info("Documentation Thread is Creating Index Page...");
		var page = createIndexPage();
		LOG.info("Documentation Thread is Writing Index Page...");
		writeFile("index.html", "", page.toHTML());
		LOG.info("Documentation Thread has finished writing Index Page!");


		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Documentation Thread has begun writing files... This may take the longest depending on your system's write speed."));
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
		LOG.info("Documentation Thread has finished writing files!");
//		LOG.info("Documentation Thread has begun writing class tree!");
//		writeFile("tree.txt", "", TREE.toString());
//		LOG.info("Documentation Thread has finished writing class tree!");

		LOG.info("Documentation Thread has finished!");
		Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("Documentation Thread has finished generating all documents!"));
    }

	private IndexPage createIndexPage() {
		Class[] eventClasses = Arrays.stream(KubeJSOffline.HELPER.getEventClasses()).flatMap((subject)
				-> TREE.findExtensionCluster(subject).getClasses().stream()
		).distinct().sorted(Comparator.comparing(Class::getName)).toList().toArray(new Class[0]);
		Map<String, Class<?>> bindings = new HashMap<>();
		Map<String, Set<ScriptType>> scriptSpecificBindings = new HashMap<>();
		for(ScriptType type : ScriptType.values()) {
			try {
				var manager = ScriptType.STARTUP.manager.get();
				for(ScriptPack pack : manager.packs.values()) {
					for(Object id : pack.scope.getAllIds()) {
						if(id == null)
							continue;
						if(id instanceof String bindingName) {
							if(!bindings.containsKey(bindingName)) {
								bindings.put(bindingName, unwrapObject(pack.scope.get(bindingName, pack.scope)));
								scriptSpecificBindings.put(bindingName, EnumSet.noneOf(ScriptType.class));
								scriptSpecificBindings.get(bindingName).add(type);
							}else {
								scriptSpecificBindings.get(bindingName).add(type);
							}
						}
					}
				}
			}catch (Throwable e) {
				LOG.error("Error while getting script manager for {}.", type, e);
			}
		}

		return new IndexPage(bindings, scriptSpecificBindings, eventClasses);
	}

	private static Class unwrapObject(Object object) {
		while(object instanceof NativeJavaObject javaObject) {
			object = javaObject.unwrap();
		}
		if(object instanceof Class clazz) {
			return clazz;
		}
		return object.getClass();
	}

	private static Path getOutputPath() {
		return Path.of("build/test/generated/html");
	}

	private static void writeFile(String fileName, String localPath, String content) {
		Path outputPath = getOutputPath().resolve(localPath).toAbsolutePath();
		if(!outputPath.toFile().exists())
			outputPath.toFile().mkdirs();

		File output = outputPath.resolve(fileName).toFile();

		try (FileOutputStream fos = new FileOutputStream(output)) {
			fos.write(content.getBytes(StandardCharsets.UTF_16));
			LOG.info("Successfully written file: " + fileName + " to " + outputPath);
		} catch (IOException e) {
			LOG.error("Failed to write file: " + fileName + " to " + outputPath, e);
		}
	}
	private static void writeFile(String fullPath, String content) {
		Path outputPath = getOutputPath().resolve(fullPath).toAbsolutePath();
		outputPath.toFile().mkdirs();
		LOG.info("Writing file: to "+outputPath);
		File output = outputPath.toFile();
		LOG.info("Link to File: "+output.toPath().toAbsolutePath());

		try {
			FileUtils.write(output, content, StandardCharsets.UTF_16, false);
		} catch (IOException e) {
			LOG.error(e);
		}
	}
}
