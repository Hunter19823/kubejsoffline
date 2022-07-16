package pie.ilikepiefoo.forge;

import cpw.mods.cl.ModularURLHandler;
import cpw.mods.niofs.union.UnionFileSystem;
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
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.jar.JarFile;

import static org.reflections.vfs.Vfs.getFile;

public class ReflectionHelperForge implements ReflectionHelper {
	static {
		Vfs.addDefaultURLTypes(new Vfs.UrlType() {
			@Override
			public boolean matches(URL url) {

				//LOG.info(url.getProtocol());
				//LOG.info(url.getFile());
				//LOG.info(url.getPath());
				return ("union".equals(url.getProtocol())) && !((url).toExternalForm().matches(".+\\.jar!/.+"));
			}

			@Override
			public Vfs.Dir createDir(URL url) throws Exception {

//				LOG.info("Path: {}", url.getPath());
//				LOG.info("File: {}", url.getFile());
//				LOG.info("URI: {}", url.toURI().toString());
//				LOG.info("Ref: {}", url.getRef());
//				LOG.info("Protocol: {}", url.getRef());
//				LOG.info("External Form: {}", url.toExternalForm());
//				LOG.info("Query: {}", url.getQuery());
//				LOG.info("Host: {}", url.getHost());
//				LOG.info("Authority: {}", url.getAuthority());

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
//					LOG.info("New Path: {}", path);
//					LOG.info("New File: {}", file);
					return new ZipDir(new JarFile(file));
				}else{
					return null;
				}
			}
		});
	}

	@Override
	public Class[] getClasses() {
//		LinkedList<ClassLoader> loaders = new LinkedList<>();
//		loaders.add(this.getClass().getClassLoader());
//		while(loaders.getLast().getParent() != null){
//			loaders.add(loaders.getLast().getParent());
//		}
//		String[] packageNames = loaders.parallelStream().map(ClassLoader::getDefinedPackages).flatMap((Arrays::stream)).map(Package::getName).toList().toArray(new String[0]);
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
}
