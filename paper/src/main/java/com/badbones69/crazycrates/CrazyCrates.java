package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.cratetypes.*;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.FireworkDamageListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.modules.ModuleManager;
import com.badbones69.crazycrates.modules.config.files.Config;
import com.badbones69.crazycrates.modules.config.files.Locale;
import com.badbones69.crazycrates.modules.config.files.menus.CrateMenuConfig;
import com.badbones69.crazycrates.support.libs.PluginSupport;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

@Singleton
public class CrazyCrates extends JavaPlugin implements Listener {

    private static CrazyCrates plugin;

    private Injector injector;

    private boolean pluginEnabled = false;

    public final Path DATA_DIRECTORY = getDataFolder().toPath().resolve("data");
    public final Path MENU_DIRECTORY = getDataFolder().toPath().resolve("menus");
    public final Path LOCALE_DIRECTORY = getDataFolder().toPath().resolve("locale");
    public final Path PLUGIN_DIRECTORY = getDataFolder().toPath();

    @Inject private CrazyLogger crazyLogger;

    @Inject private CrazyManager crazyManager;

    @Inject private FileManager fileManager;

    @Override
    public void onEnable() {
        try {
            plugin = this;

            ModuleManager moduleManager = new ModuleManager(new CrazyManager());

            injector = moduleManager.createInjector();

            injector.injectMembers(this);

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
                    .registerCustomFilesFolder("/data")
                    .setup();

            Locale.reload(LOCALE_DIRECTORY, Config.LANGUAGE_FILE, crazyLogger);

            // Crate Menus.
            CrateMenuConfig.reload(MENU_DIRECTORY, crazyLogger);

            if (Config.TOGGLE_METRICS) new Metrics(this, 4514);
        } catch (Exception e) {
            e.printStackTrace();

            pluginEnabled = false;

            return;
        }

        // enable();

        pluginEnabled = true;
    }

    @Inject private CosmicCrate cosmicCrate;
    @Inject private QuickCrate quickCrate;
    @Inject private CrateOnTheGo crateOnTheGo;
    @Inject private CsgoCrate csgoCrate;
    @Inject private QuadCrate quadCrate;
    @Inject private RouletteCrate rouletteCrate;
    @Inject private WarCrate warCrate;
    @Inject private WheelCrate wheelCrate;
    @Inject private WonderCrate wonderCrate;

    @Override
    public void onDisable() {
        if (!pluginEnabled) return;

        SessionManager.endCrates();

        quickCrate.removeAllRewards();

        if (crazyManager.getHologramController() != null) crazyManager.getHologramController().removeAllHolograms();

        injector = null;
    }

    @Inject private PlaceholderAPISupport placeholderAPISupport;

    @Inject private BrokeLocationsListener brokeLocationsListener;
    @Inject private CrateControlListener crateControlListener;
    @Inject private FireworkDamageListener fireworkDamageListener;
    @Inject private MiscListener miscListener;

    private void enable() {

        crazyManager.loadCrates();

        PluginManager pluginManager = getServer().getPluginManager();

        if (!crazyManager.getBrokeCrateLocations().isEmpty()) pluginManager.registerEvents(brokeLocationsListener, this);

        pluginManager.registerEvents(crateControlListener, this);
        pluginManager.registerEvents(fireworkDamageListener, this);
        pluginManager.registerEvents(miscListener, this);

        pluginManager.registerEvents(cosmicCrate, this);
        pluginManager.registerEvents(crateOnTheGo, this);
        pluginManager.registerEvents(csgoCrate, this);
        pluginManager.registerEvents(quadCrate, this);
        pluginManager.registerEvents(quickCrate, this);
        pluginManager.registerEvents(rouletteCrate, this);
        pluginManager.registerEvents(warCrate, this);
        pluginManager.registerEvents(wheelCrate, this);
        pluginManager.registerEvents(wonderCrate, this);

        if (PluginSupport.PLACEHOLDERAPI.isPluginLoaded()) placeholderAPISupport.register();
    }

    public static CrazyCrates getPlugin() {
        return plugin;
    }
}