package pie.ilikepiefoo.kubejsoffline.forge;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.loading.FMLPaths;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;
import pie.ilikepiefoo.kubejsoffline.util.ReflectionHelper;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		Set<String> packageSet = Arrays.stream(packages).parallel().map(Package::getName).collect(Collectors.toSet());
		LOG.info("Full Package List: " + packageSet);
		return findModClasses()
				.parallelStream()
				.filter(name -> name.contains("."))
				.filter(name -> (!Platform.getEnv().isClient() && !name.contains("client")))
				.filter(name -> packageSet.parallelStream().anyMatch(name::startsWith))
				.map(ReflectionHelperForge::findClass).toArray(Class[]::new);
	}
	private static Class<?> findClass(String name) {
		try {
			return Class.forName(name);
		} catch (Throwable ignored) {}
		return null;
	}

	public static final List<String> findModClasses() {
		List<String> output = new ArrayList<>();
		// Open the mods folder
		File modsFolder = FMLPaths.MODSDIR.get().toFile();
		if (!modsFolder.exists()) {
			return List.of();
		}
		// Get all the files in the mods folder
		File[] files = modsFolder.listFiles();
		if (files == null) {
			return List.of();
		}
		Stream.of(files)
				.parallel()
				.forEach(file -> output.addAll(searchFile(file)));
		// Search for all the classes in the mods folder
		return output;
	}

	public static List<String> searchFile(File file) {
		if(file == null) return List.of();
		if(!file.exists()) return List.of();
		List<String> classes = new ArrayList<>();
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (File f : files) {
					classes.addAll(searchFile(f));
				}
			}
		} else if(file.getName().endsWith(".jar")) {
			classes.addAll(searchJar(file));
		}
		return classes;
	}

	public static List<String> searchJar(File file) {
		try (JarFile jarFile = new JarFile(file)){
			Enumeration<JarEntry> entries = jarFile.entries();
			List<String> classes = new ArrayList<>();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				if (name.endsWith(".class") && !name.contains("$")) {
					classes.add(name.replace('/', '.').substring(0, name.length() - 6));
				}
			}
			return classes;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return List.of();
	}

	@Override
	public Class[] getEventClasses() {
		return new Class[] {EventJS.class, Event.class};
	}

	/**
	 * Get the path to the working directory of the current platform.
	 */
	@Override
	public Path getWorkingDirectory() {
		return FMLPaths.GAMEDIR.get();
	}


}
