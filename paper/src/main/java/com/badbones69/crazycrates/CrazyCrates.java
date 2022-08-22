package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.modules.PluginModule;
import com.badbones69.crazycrates.modules.config.files.ConfigFile;
import com.badbones69.crazycrates.modules.config.files.LocaleFile;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

@Singleton
public class CrazyCrates extends JavaPlugin implements Listener {

    private Injector injector;

    private boolean pluginEnabled = false;

    //@Inject private CrazyManager crazyManager;
    @Inject private FileManager fileManager;

    public final Path DATA_DIRECTORY = getDataFolder().toPath().resolve("data");
    public final Path LOCALE_DIRECTORY = getDataFolder().toPath().resolve("locale");
    public final Path PLUGIN_DIRECTORY = getDataFolder().toPath();

    //@Inject private CosmicCrate cosmicCrate;
    //@Inject private CrateOnTheGo crateOnTheGo;
    //@Inject private CsgoCrate csgoCrate;
    //@Inject private FireCrackerCrate fireCrackerCrate;
    //@Inject private QuadCrate quadCrate;
    //@Inject private QuickCrate quickCrate;
    //@Inject private RouletteCrate rouletteCrate;
    //@Inject private WarCrate warCrate;
    //@Inject private WheelCrate wheelCrate;
    //@Inject private WonderCrate wonderCrate;

    @Override
    public void onEnable() {
        try {
            if (!getDataFolder().exists()) getDataFolder().mkdirs();

            PluginModule module = new PluginModule(this);

            injector = module.createInjector();

            injector.injectMembers(this);
            
            String cratesFolder = "/crates";
            String schematicFolder = "/schematics";
            String localeFolder = "/locale";

            // TODO() Add more crate types.
            fileManager.logInfo(true)
                    // Crate Examples.
                    .registerDefaultGenerateFiles("crate-example.yml", cratesFolder, cratesFolder)

                    // Locale Files.
                    .registerDefaultGenerateFiles("locale-en.yml", localeFolder, localeFolder)
                    .registerDefaultGenerateFiles("locale-cz.yml", localeFolder, localeFolder)
                    .registerDefaultGenerateFiles("locale-sp.yml", localeFolder, localeFolder)

                    // NBT Files.
                    .registerDefaultGenerateFiles("classic.nbt", schematicFolder, schematicFolder)
                    .registerDefaultGenerateFiles("nether.nbt", schematicFolder, schematicFolder)
                    .registerDefaultGenerateFiles("outdoors.nbt", schematicFolder, schematicFolder)
                    .registerDefaultGenerateFiles("sea.nbt", schematicFolder, schematicFolder)
                    .registerDefaultGenerateFiles("soul.nbt", schematicFolder, schematicFolder)
                    .registerDefaultGenerateFiles("wooden.nbt", schematicFolder, schematicFolder)

                    // Directories.
                    .registerCustomFilesFolder("/crates")
                    .registerCustomFilesFolder("/schematics")
                    .registerCustomFilesFolder("/locale")
                    .setup(this);

            // Create default config.
            saveDefaultConfig();

            ConfigFile.reload(PLUGIN_DIRECTORY, this, crazyLogger);
            LocaleFile.reload(LOCALE_DIRECTORY, ConfigFile.LANGUAGE_FILE, this, crazyLogger);

            new Metrics(this, 4514);

            // Add extra messages.
            //Messages.addMissingMessages();

            //String metricsPath = FileManager.Files.CONFIG.getFile().getString("Settings.Toggle-Metrics");
            //boolean metricsEnabled = Files.CONFIG.getFile().getBoolean("Settings.Toggle-Metrics");

            //if (metricsPath != null) {
            //    if (metricsEnabled) new Metrics(this, 4514);
            //} else {
            //    getLogger().warning("Metrics was automatically enabled.");
            //    getLogger().warning("Please add Toggle-Metrics: false to the top of your config.yml.");
            //    getLogger().warning("https://github.com/Crazy-Crew/Crazy-Crates/blob/main/src/main/resources/config.yml");
            //    getLogger().warning("An example if confused is linked above.");
            //}

        } catch (Exception e) {
            e.printStackTrace();

            pluginEnabled = false;

            return;
        }

        enable();

        pluginEnabled = true;
    }

    @Override
    public void onDisable() {
        if (!pluginEnabled) return;

        //SessionManager.endCrates();

        //quickCrate.removeAllRewards();

        //if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeAllHolograms();

        injector = null;
    }

    private void enable() {

        //crazyManager.loadCrates();

        //if (!crazyManager.getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(new BrokeLocationsListener(), this);

        //if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded(this)) new PlaceholderAPISupport().register();
    }
}