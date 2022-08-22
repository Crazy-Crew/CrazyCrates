package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.modules.PluginModule;
import com.badbones69.crazycrates.modules.config.files.Config;
import com.badbones69.crazycrates.modules.config.files.Locale;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
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
    public final Path MENU_DIRECTORY = getDataFolder().toPath().resolve("menus");
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

    @Inject private CrazyLogger crazyLogger;

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
                    //.registerDefaultGenerateFiles("crate-example.yml", cratesFolder, cratesFolder)

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
                    .registerCustomFilesFolder("/menus")
                    .setup(this);

            // Create default config.
            saveDefaultConfig();

            Config.reload(PLUGIN_DIRECTORY, crazyLogger);
            Locale.reload(LOCALE_DIRECTORY, Config.LANGUAGE_FILE, crazyLogger);

            // Crate Menus.
            //CrateMenuFile.reload(MENU_DIRECTORY, this);

            new Metrics(this, 4514);

            if (Config.TOGGLE_METRICS) new Metrics(this, 4514);
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