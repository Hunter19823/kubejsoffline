package pie.ilikepiefoo.util;


import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.EventJS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;


public class ClassFinderTest {
	public static final Logger LOG = LogManager.getLogger();

	@Test
	public void SearchKubeJS() {
		Class[] allKubeJSClasses = findAllClasses("dev.latvian.mods.kubejs");
		LOG.info("Size of Classes Found {}", allKubeJSClasses.length);
		//ClassFinder.INSTANCE.addToSearch(allKubeJSClasses);
		ClassFinder.INSTANCE.addToSearch(KubeJS.class);
		ClassTree tree = new ClassTree();
		ClassFinder.INSTANCE.onSearched(tree::addClass);
		ClassFinder.DEBUG = true;
		while(!ClassFinder.INSTANCE.isFinished()) {
			LOG.info("Class Count {}", ClassFinder.INSTANCE.CLASS_SEARCH.size());
			//ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().filter(entry->entry.getValue() == ClassFinder.SearchState.IN_QUEUE).forEach((entry)->ClassFinder.LOG.info("Queued Class:\t{}",entry.getKey()));
			ClassFinder.INSTANCE.searchCurrentDepth();
		}
		LOG.info(tree);
	}

	public static void main(String[] args) {
		new ClassFinderTest().SearchKubeJS();
	}

	public Class[] findAllClasses(String packageName) {
		Reflections reflections = new Reflections(packageName);
		return reflections.getSubTypesOf(EventJS.class).toArray(new Class[0]);
	}
}