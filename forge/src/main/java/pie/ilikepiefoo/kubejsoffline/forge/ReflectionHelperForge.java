package pie.ilikepiefoo.kubejsoffline.forge;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.event.EventJS;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.loading.FMLPaths;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;
import pie.ilikepiefoo.kubejsoffline.util.ReflectionHelper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.JarFile;

public class ReflectionHelperForge implements ReflectionHelper {
	static {
		Vfs.addDefaultURLTypes(new Vfs.UrlType() {
			@Override
			public boolean matches(URL url) {
				return ("union".equals(url.getProtocol())) && !((url).toExternalForm().matches(".+\\.jar!/.+"));
			}

			@Override
			public Vfs.Dir createDir(URL url) throws Exception {
				String path = url.toURI().getPath();
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
		LOG.info("Added new union URL Type to VFS");
		Vfs.addDefaultURLTypes(Vfs.DefaultUrlTypes.directory);
		LOG.info("Added new directory Url Types to VFS");
		LOG.info("Now attempting to load JavaAssist ClassFile class");
		try {
			ClassFile file = new ClassFile(false, "test", null);
			file.addField(new FieldInfo(file.getConstPool(), "test", "I"));
			file.addMethod(new MethodInfo(file.getConstPool(), "test", "()V"));
			LOG.info("Successfully loaded JavaAssist ClassFile class!");
		} catch (Throwable e) {
			LOG.error("Failed to load JavaAssist ClassFile class. KubeJS Offline Functionality will break past this point!", e);
		}
	}

	@Override
	public Class[] getClasses() {
		return useReflections();
	}
	private static Class[] useReflections() {
		Package[] packages = Package.getPackages();
		String[] packageNames = Arrays.stream(packages).parallel().map(Package::getName).toList().toArray(new String[0]);
		URL modsFolder;
		try {
			modsFolder = Platform.getModsFolder().toUri().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		Configuration configuration = new ConfigurationBuilder()
				.setParallel(true)
				.forPackages(packageNames)
				.addUrls(modsFolder)
				.setScanners(Scanners.SubTypes.filterResultsBy(pred->true), Scanners.Resources);
		Reflections reflections = new Reflections(configuration);
		return reflections.getSubTypesOf(Object.class).toArray(new Class[0]);
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
