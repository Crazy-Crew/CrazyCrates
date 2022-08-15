package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.modules.PluginModule;
import com.badbones69.crazycrates.modules.config.files.ConfigFile;
import com.badbones69.crazycrates.modules.config.files.LocaleFile;
import com.badbones69.crazycrates.utilities.FileUtils;
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

    @Inject private CrazyManager crazyManager;
    @Inject private FileManager fileManager;

    @Inject private FileUtils fileUtils;

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

    @Inject private ConfigFile configFile;
    @Inject private LocaleFile localeFile;

    @Override
    public void onEnable() {
        try {

            if (!getDataFolder().exists()) getDataFolder().mkdirs();

            if (!LOCALE_DIRECTORY.toFile().exists()) LOCALE_DIRECTORY.toFile().mkdirs();

            PluginModule module = new PluginModule(this);

            injector = module.createInjector();

            injector.injectMembers(this);

            saveDefaultConfig();
            fileUtils.create(configFile, localeFile, LOCALE_DIRECTORY, PLUGIN_DIRECTORY, this);

            fileManager.logInfo(true)
                    .registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                    .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                    .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                    .registerCustomFilesFolder("/crates")
                    .registerCustomFilesFolder("/schematics")
                    .setup(this);

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