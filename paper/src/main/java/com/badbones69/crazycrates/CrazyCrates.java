package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.cratetypes.QuadCrate;
import com.badbones69.crazycrates.modules.config.files.Config;
import com.badbones69.crazycrates.modules.config.files.Locale;
import com.badbones69.crazycrates.modules.config.files.menus.CrateMenuConfig;
import com.badbones69.crazycrates.support.libs.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.support.structures.blocks.ChestStateHandler;
import com.badbones69.crazycrates.utilities.AdventureUtils;
import com.badbones69.crazycrates.utilities.CommonUtils;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

public class CrazyCrates extends JavaPlugin implements Listener {

    private static CrazyCrates plugin;

    private boolean pluginEnabled = false;

    public final Path DATA_DIRECTORY = getDataFolder().toPath().resolve("data");
    public final Path MENU_DIRECTORY = getDataFolder().toPath().resolve("menus");
    public final Path LOCALE_DIRECTORY = getDataFolder().toPath().resolve("locale");
    public final Path PLUGIN_DIRECTORY = getDataFolder().toPath();

    private FileManager fileManager;
    private CrazyManager crazyManager;
    private CrazyLogger crazyLogger;
    private Methods methods;
    private CommonUtils commonUtils;
    private AdventureUtils adventureUtils;

    @Override
    public void onEnable() {
        try {
            plugin = this;

            adventureUtils = new AdventureUtils();

            crazyLogger = new CrazyLogger(adventureUtils);

            fileManager = new FileManager(crazyLogger);

            methods = new Methods();

            commonUtils = new CommonUtils(crazyLogger, crazyManager = new CrazyManager(crazyLogger, fileManager, methods), methods);

            if (!getDataFolder().exists()) getDataFolder().mkdirs();

            //data.load();
            
            String cratesFolder = "/crates";
            String schematicFolder = "/schematics";
            String localeFolder = "/locale";
            String menuFolder = "/menus";

            // Create default config.
            saveDefaultConfig();

            Config.reload(PLUGIN_DIRECTORY, crazyLogger);

            // TODO() Add more crate types.
            fileManager.toggleLogging(Config.TOGGLE_VERBOSE)
                    // Crate Examples.
                    //.registerDefaultGenerateFiles("crate-example.yml", cratesFolder, cratesFolder)

                    // Crate Menu Files.
                    .registerDefaultGenerateFiles("crate-menu.yml", menuFolder, menuFolder)
                    .registerDefaultGenerateFiles("preview-menu.yml", menuFolder, menuFolder)

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

            Locale.reload(LOCALE_DIRECTORY, Config.LANGUAGE_FILE, crazyLogger);

            // Crate Menus.
            CrateMenuConfig.reload(MENU_DIRECTORY, crazyLogger);

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

        SessionManager.endCrates();

        //quickCrate.removeAllRewards();

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeAllHolograms();
    }

    private void enable() {

        crazyManager.loadCrates();

        PluginManager pluginManager = getServer().getPluginManager();

        //if (!crazyManager.getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(brokeLocationsListener, this);

        //pluginManager.registerEvents(crateControlListener, this);
        //pluginManager.registerEvents(fireworkDamageListener, this);
        //pluginManager.registerEvents(miscListener, this);

        //pluginManager.registerEvents(cosmicCrate, this);
        //pluginManager.registerEvents(crateOnTheGo, this);
        //pluginManager.registerEvents(csgoCrate, this);
        //pluginManager.registerEvents(quadCrate, this);
        //pluginManager.registerEvents(quickCrate, this);
        //pluginManager.registerEvents(rouletteCrate, this);
        //pluginManager.registerEvents(warCrate, this);
        //pluginManager.registerEvents(wheelCrate, this);
        //pluginManager.registerEvents(wonderCrate, this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) new PlaceholderAPISupport(crazyManager).register();
    }

    public static CrazyCrates getInstance() {
        return plugin;
    }
}