package pie.ilikepiefoo;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.html.page.ClassPage;
import pie.ilikepiefoo.html.page.ClassTreePage;
import pie.ilikepiefoo.html.page.MainPage;
import pie.ilikepiefoo.util.ClassCluster;
import pie.ilikepiefoo.util.ClassFinder;
import pie.ilikepiefoo.util.ClassTree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

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
		while(!ClassFinder.INSTANCE.isFinished()) {
			LOG.info("Class Count {}", ClassFinder.INSTANCE.CLASS_SEARCH.size());
			//ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().filter(entry->entry.getValue() == ClassFinder.SearchState.IN_QUEUE).forEach((entry)->ClassFinder.LOG.info("Queued Class:\t{}",entry.getKey()));
			ClassFinder.INSTANCE.searchCurrentDepth();
		}
		LOG.info("Documentation Thread has finished searching classes!");
		LOG.info("Documentation Thread has begun memory compression!");
		TREE.compress();
		LOG.info("Documentation Thread has finished memory compression!");
//		MainPage mainPage = new MainPage();
		//mainPage.SIDE_NAV.add(mainPage.addCluster(TREE.getFileRoot(),"FileRoot"));
//		mainPage.SIDE_NAV.add(mainPage.addCluster(TREE.getExtensionRoot(),"ExtensionRoot"));

		LOG.info("Documentation Thread has begun writing files!");
		TREE.getFileRoot().stream().parallel().flatMap((cluster) -> cluster.getClasses().parallelStream()).forEachOrdered((target) -> {
			var url = ClassPage.getURL(target);
			LOG.info("Now Generating Documentation for {} at URL {}", target.getName(), url);
			var file_name = url.substring(url.lastIndexOf('/')+1);
			var file_path = url.substring(1, url.lastIndexOf('/'));
			writeFile(file_name, file_path, new ClassPage(target).toHTML());
		});
		LOG.info("Documentation Thread has finished writing files!");
		LOG.info("Documentation Thread has begun writing class tree!");
		writeFile("tree.txt", "", TREE.toString());
		LOG.info("Documentation Thread has finished writing class tree!");

		LOG.info("Documentation Thread has finished!");
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
