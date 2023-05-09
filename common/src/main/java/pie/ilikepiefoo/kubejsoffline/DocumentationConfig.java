package pie.ilikepiefoo.kubejsoffline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.util.RelationType;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.Set;

public class DocumentationConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Logger LOGGER = LogManager.getLogger();
	private static Config INSTANCE;

	public static synchronized Config getInstance() {
		if (null == INSTANCE) {
			INSTANCE = reloadConfig();
		}
		return INSTANCE;
	}

	private static synchronized Config reloadConfig() {
		final Path config = Platform.getConfigFolder().resolve(KubeJSOffline.MOD_ID + "-config.json");
		if (config.toFile().exists()) {
			try (final FileReader reader = new FileReader(config.toFile(), StandardCharsets.UTF_8)) {
				INSTANCE = GSON.fromJson(reader, Config.class);
			} catch (final Exception e) {
				LOGGER.error("Failed to load config file!", e);
				INSTANCE = new Config();
			}
		} else {
			INSTANCE = new Config();
			try (final FileWriter writer = new FileWriter(config.toFile(), StandardCharsets.UTF_8)) {
				GSON.toJson(INSTANCE, writer);
			} catch (final Exception e) {
				LOGGER.error("Failed to create config file!", e);
			}
		}
		return INSTANCE;
	}

	public static synchronized void clearInstance() {
		INSTANCE = null;
	}


	public static class Config {
		public boolean saveAnyRelationTypeData = true;
		public Set<RelationType> enabledRelationTypes = EnumSet.allOf(RelationType.class);
	}

}
