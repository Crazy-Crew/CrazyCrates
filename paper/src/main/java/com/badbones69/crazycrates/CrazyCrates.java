package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.enums.other.Plugins;
import com.badbones69.crazycrates.common.Server;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.listeners.DataListener;
import com.badbones69.crazycrates.listeners.crates.CrateInteractListener;
import com.badbones69.crazycrates.listeners.items.PaperInteractListener;
import com.badbones69.crazycrates.managers.data.DataManager;
import com.badbones69.crazycrates.support.MetricsWrapper;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.crates.types.CosmicCrateListener;
import com.badbones69.crazycrates.listeners.crates.CrateOpenListener;
import com.badbones69.crazycrates.listeners.crates.types.MobileCrateListener;
import com.badbones69.crazycrates.listeners.crates.types.QuadCrateListener;
import com.badbones69.crazycrates.listeners.crates.types.WarCrateListener;
import com.badbones69.crazycrates.listeners.other.EntityDamageListener;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import com.badbones69.crazycrates.managers.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.FusionApi;
import com.ryderbelserion.api.enums.FileType;
import com.ryderbelserion.paper.files.FileManager;
import com.ryderbelserion.util.Methods;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import static com.badbones69.crazycrates.utils.MiscUtils.registerPermissions;

public class CrazyCrates extends JavaPlugin {

    public static CrazyCrates getPlugin() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    private final long startTime;
    private final Timer timer;

    public CrazyCrates() {
        this.startTime = System.currentTimeMillis();

        this.timer = new Timer();
    }

    private final FusionApi api = FusionApi.get();

    private FileManager fileManager;
    private DataManager dataManager;

    private BukkitUserManager userManager;

    private InventoryManager inventoryManager;
    private CrateManager crateManager;
    private HeadDatabaseAPI hdb;

    private Server instance;

    private MetricsWrapper metrics;

    @Override
    public void onEnable() {
        this.instance = new Server(getDataFolder());
        this.instance.apply();

        this.api.enable(this);

        this.fileManager = this.api.getFileManager();

        this.fileManager.addFile("data.yml", FileType.YAML).addFile("locations.yml", FileType.YAML)
                .addFile("respin-gui.yml", "guis", false, FileType.YAML)
                .addFile("crates.log", "logs", false, FileType.NONE)
                .addFile("keys.log", "logs", false, FileType.NONE)
                .addFolder("crates", FileType.YAML)
                .addFolder("schematics", FileType.NONE);

        MiscUtils.janitor();
        MiscUtils.save();

        // Register permissions that we need.
        registerPermissions();

        if (Plugins.head_database.isEnabled()) {
            this.hdb = new HeadDatabaseAPI();
        }

        this.dataManager = new DataManager().init();

        this.inventoryManager = new InventoryManager();
        this.crateManager = new CrateManager();

        this.userManager = new BukkitUserManager();

        this.instance.setUserManager(this.userManager);

        // Load holograms.
        this.crateManager.loadHolograms();

        // Load the buttons.
        this.inventoryManager.loadButtons();

        // Load the crates.
        this.crateManager.loadCrates();

        if (ConfigManager.getConfig().getProperty(ConfigKeys.toggle_metrics)) {
            this.metrics = new MetricsWrapper(4514);
            this.metrics.start();
        }

        final PluginManager pluginManager = this.getServer().getPluginManager();

        pluginManager.registerEvents(new DataListener(), this);

        List.of(
                // Other listeners.
                new BrokeLocationsListener(),
                new EntityDamageListener(),
                new MobileCrateListener(),
                new CosmicCrateListener(),
                new QuadCrateListener(),
                new WarCrateListener(),
                new MiscListener(),

                new CrateInteractListener(),
                new CrateControlListener(),
                new CrateOpenListener(),

                new PaperInteractListener()
        ).forEach(listener -> pluginManager.registerEvents(listener, this));

        this.crateManager.loadCustomItems();

        if (Plugins.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) getComponentLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) {
            // Print dependency garbage
            for (final Plugins value : Plugins.values()) {
                if (value.isEnabled()) {
                    getComponentLogger().info(Methods.parse("<bold><gold>" + value.getName() + " <green>FOUND"));
                } else {
                    getComponentLogger().info(Methods.parse("<bold><gold>" + value.getName() + " <red>NOT FOUND"));
                }
            }

            getComponentLogger().info("Done ({})!", String.format(Locale.ROOT, "%.3fs", (double) (System.nanoTime() - this.startTime) / 1.0E9D));
        }
    }

    @Override
    public void onDisable() {
        this.api.disable();

        // Cancel the tasks
        getServer().getGlobalRegionScheduler().cancelTasks(this);
        getServer().getAsyncScheduler().cancelTasks(this);

        // Cancel the timer task.
        this.timer.cancel();

        // Clean up any mess we may have left behind.
        if (this.crateManager != null) {
            this.crateManager.purgeRewards();

            final HologramManager holograms = this.crateManager.getHolograms();

            if (holograms != null) {
                holograms.purge(true);
            }
        }

        if (this.instance != null) {
            this.instance.disable();
        }

        MiscUtils.janitor();
    }

    public final InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    public final CrateManager getCrateManager() {
        return this.crateManager;
    }

    public @Nullable final HeadDatabaseAPI getHdb() {
        if (this.hdb == null) {
            return null;
        }

        return this.hdb;
    }

    public final Server getInstance() {
        return this.instance;
    }

    public @Nullable final MetricsWrapper getMetrics() {
        return this.metrics;
    }

    public final Timer getTimer() {
        return this.timer;
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public FusionApi getApi() {
        return this.api;
    }
}