package pie.ilikepiefoo.kubejsoffline;


import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pie.ilikepiefoo.kubejsoffline.command.DocumentCommand;

public class EventHandler {
    public static final Logger LOG = LogManager.getLogger();
    public static final String TEST_WORLD_NAME = "TestWorld (%s)".formatted(SharedConstants.getCurrentVersion().getName().replace(".","_"));

    public static void init() {
        DocumentCommand.EVENT.register(new DocumentCommand());
        if (KubeJSOffline.isAutomaticGenerationEnabled()) {
            LOG.info("Automatic generation of KubeJS documentation is enabled.");
            ClientGuiEvent.SET_SCREEN.register(EventHandler::onTitleScreen);
            PlayerEvent.PLAYER_JOIN.register(player -> {
                LOG.info("Player {} has joined the server. Running KubeJS documentation generation command.", player.getGameProfile().getName());
                player.kjs$runCommand("kubejsoffline");
            });
        } else {
            LOG.info("Automatic generation of KubeJS documentation is disabled.");
        }
    }

    public static CompoundEventResult<Screen> onTitleScreen(Screen screen) {
        if (!(screen instanceof AccessibilityOnboardingScreen || screen instanceof TitleScreen)) {
            LOG.info("Skipping screen {}", screen == null ? "(null)" : screen.getClass().getName());
            return CompoundEventResult.pass();
        }
        LOG.info("Title Screen detected. Opening Select World Screen.");
        Minecraft.getInstance().setScreen(new SelectWorldScreen(screen));
        Minecraft.getInstance().getResourcePackRepository().reload();
        createTestWorld(screen);
        return CompoundEventResult.pass();
    }

    public static void createTestWorld(Screen screen) {
        var flow = Minecraft
                .getInstance()
                .createWorldOpenFlows();
        if (Minecraft.getInstance().getLevelSource().levelExists(TEST_WORLD_NAME)) {
            LOG.info("The world '{}' already exists. Skipping world creation.", TEST_WORLD_NAME);
            flow.loadLevel(screen, TEST_WORLD_NAME);
        } else {
            LOG.info("Creating a fresh level called '{}' for KubeJS documentation generation.", TEST_WORLD_NAME);
            // Create a fresh level with the specified settings.
            flow.createFreshLevel(
                    TEST_WORLD_NAME,
                    createTestWorldLevelSettings(),
                    new WorldOptions(
                            0L,
                            false,
                            false
                    ),
                    EventHandler::createTestWorldDimensions
            );
        }
    }

    public static WorldDimensions createTestWorldDimensions(RegistryAccess registryAccess) {
        var flatLevelSettings = FlatLevelGeneratorSettings.getDefault(
                registryAccess.lookupOrThrow(Registries.BIOME),
                registryAccess.lookupOrThrow(Registries.STRUCTURE_SET),
                registryAccess.lookupOrThrow(Registries.PLACED_FEATURE)
        );
        flatLevelSettings.getLayers().clear();
        flatLevelSettings.getLayersInfo().add(new FlatLayerInfo(1, Blocks.BEDROCK));
        flatLevelSettings.updateLayers();
        return registryAccess
                .registryOrThrow(Registries.WORLD_PRESET)
                .getHolderOrThrow(WorldPresets.FLAT)
                .value()
                .createWorldDimensions()
                .replaceOverworldGenerator(
                        registryAccess,
                        new FlatLevelSource(flatLevelSettings)
                );
    }


    public static LevelSettings createTestWorldLevelSettings() {
        return new LevelSettings(
                "KubeJS Offline Test World",
                GameType.CREATIVE,
                false,
                Difficulty.PEACEFUL,
                true,
                createTestWorldGameRules(),
                new WorldDataConfiguration(
                        DataPackConfig.DEFAULT,
                        FeatureFlags.VANILLA_SET
                )
        );
    }

    public static GameRules createTestWorldGameRules() {
        GameRules gameRules = new GameRules();
        gameRules.getRule(GameRules.RULE_DAYLIGHT).set(false, null);
        gameRules.getRule(GameRules.RULE_DISABLE_RAIDS).set(true, null);
        gameRules.getRule(GameRules.RULE_DISABLE_RAIDS).set(true, null);
        gameRules.getRule(GameRules.RULE_DOBLOCKDROPS).set(false, null);
        gameRules.getRule(GameRules.RULE_DOENTITYDROPS).set(false, null);
        gameRules.getRule(GameRules.RULE_DOFIRETICK).set(false, null);
        gameRules.getRule(GameRules.RULE_DOINSOMNIA).set(false, null);
        gameRules.getRule(GameRules.RULE_DOMOBLOOT).set(false, null);
        gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
        gameRules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, null);
        gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, null);
        gameRules.getRule(GameRules.RULE_DO_VINES_SPREAD).set(false, null);
        gameRules.getRule(GameRules.RULE_KEEPINVENTORY).set(true, null);
        gameRules.getRule(GameRules.RULE_LAVA_SOURCE_CONVERSION).set(false, null);
        gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(false, null);
        gameRules.getRule(GameRules.RULE_RANDOMTICKING).set(0, null);
        gameRules.getRule(GameRules.RULE_SPAWN_RADIUS).set(1, null);
        gameRules.getRule(GameRules.RULE_WATER_SOURCE_CONVERSION).set(false, null);
        gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
        return gameRules;
    }
}
