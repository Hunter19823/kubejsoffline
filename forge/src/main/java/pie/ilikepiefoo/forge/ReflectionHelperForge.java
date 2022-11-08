package pie.ilikepiefoo.forge;

import cpw.mods.cl.ModularURLHandler;
import cpw.mods.niofs.union.UnionFileSystem;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.loading.ModJarURLHandler;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.JarInputDir;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;
import pie.ilikepiefoo.util.ReflectionHelper;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.jar.JarFile;

import static org.reflections.vfs.Vfs.getFile;

public class ReflectionHelperForge implements ReflectionHelper {
	static {
		Vfs.addDefaultURLTypes(new Vfs.UrlType() {
			@Override
			public boolean matches(URL url) {

				return ("union".equals(url.getProtocol())) && !((url).toExternalForm().matches(".+\\.jar!/.+"));
			}

			@Override
			public Vfs.Dir createDir(URL url) throws Exception {

				String path = url.getPath();
				if(path.indexOf('/') == 0) {
					path = path.substring(1);
				}
				int jar = path.indexOf(".jar");
				if(jar != -1) {
					path = path.substring(0,jar+4);
				}
				File file = new File(path);
				if(file.exists()){
					return new ZipDir(new JarFile(file));
				}else{
					return null;
				}
			}
		});
	}

	@Override
	public Class[] getClasses() {
		Package[] packages = Package.getPackages();
		String[] packageNames = Arrays.stream(packages).parallel().map(Package::getName).toList().toArray(new String[0]);
		LOG.info(Arrays.toString(packageNames));
		Configuration configuration = new ConfigurationBuilder()
				.setParallel(true)
				.forPackages(packageNames)
				.setScanners(Scanners.SubTypes.filterResultsBy(pred->true), Scanners.Resources);
		Reflections reflections = new Reflections(configuration);
		Class[] classes = reflections.getSubTypesOf(Object.class).toArray(new Class[0]);
		return classes;
	}

	@Override
	public Class[] getEventClasses() {
		return new Class[] {EventJS.class, Event.class};
	}


}
