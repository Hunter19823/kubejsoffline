package pie.ilikepiefoo.util;


import dev.latvian.mods.kubejs.event.EventJS;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;


public class ClassFinderTest {
	@Test
	public void SearchKubeJS() {
		Class[] allKubeJSClasses = findAllClasses("dev.latvian.mods.kubejs");
		ClassFinder.LOG.info("Size of Classes Found {}", allKubeJSClasses.length);
		ClassFinder.INSTANCE.addToSearch(allKubeJSClasses);
		ClassFinder.DEBUG = false;
		while(!ClassFinder.INSTANCE.isFinished()) {
			ClassFinder.LOG.info("Class Count {}", ClassFinder.INSTANCE.CLASS_SEARCH.size());
			//ClassFinder.INSTANCE.CLASS_SEARCH.entrySet().parallelStream().filter(entry->entry.getValue() == ClassFinder.SearchState.IN_QUEUE).forEach((entry)->ClassFinder.LOG.info("Queued Class:\t{}",entry.getKey()));
			ClassFinder.INSTANCE.searchCurrentDepth();
		}
	}

	public static void main(String[] args) {
		new ClassFinderTest().SearchKubeJS();
	}

	public Class[] findAllClasses(String packageName) {
		Reflections reflections = new Reflections(packageName);
		return reflections.getSubTypesOf(EventJS.class).toArray(new Class[0]);
	}
}