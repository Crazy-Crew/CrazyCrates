package com.badbones69.crazycrates;

import com.badbones69.crazycrates.listeners.crates.CrateEditorListener;
import com.badbones69.crazycrates.common.Server;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.listeners.crates.CrateInteractListener;
import com.badbones69.crazycrates.listeners.items.NexoInteractListener;
import com.badbones69.crazycrates.listeners.items.PaperInteractListener;
import com.badbones69.crazycrates.support.MetricsWrapper;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.commands.CommandManager;
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
import com.ryderbelserion.vital.files.enums.FileType;
import com.ryderbelserion.vital.paper.VitalPaper;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.utils.Methods;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import static com.badbones69.crazycrates.utils.MiscUtils.registerPermissions;

@ApiStatus.Internal
public class CrazyCrates extends JavaPlugin {

    @ApiStatus.Internal
    public static CrazyCrates getPlugin() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    private final VitalPaper vital;
    private final Timer timer;
    private final long startTime;

    public CrazyCrates() {
        this.startTime = System.nanoTime();

        this.vital = new VitalPaper(this);

        this.timer = new Timer();
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private HeadDatabaseAPI api;

    private Server instance;

    private MetricsWrapper metrics;

    @Override
    public void onEnable() {
        this.instance = new Server(getDataFolder());
        this.instance.apply();

        this.vital.getFileManager().addFile("locations.yml", FileType.YAML).addFile("data.yml", FileType.YAML).addFile("respin-gui.yml", "guis", false, FileType.YAML)
                .addFile("crates.log", "logs", false, FileType.NONE)
                .addFile("keys.log", "logs", false, FileType.NONE)
                .addFolder("crates", FileType.YAML)
                .addFolder("schematics", FileType.NONE);

        MiscUtils.janitor();
        MiscUtils.save();

        // Register permissions that we need.
        registerPermissions();

        if (Support.head_database.isEnabled()) {
            this.api = new HeadDatabaseAPI();
        }

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
            this.metrics = new MetricsWrapper(this, 4514, true);
            this.metrics.start();
        }

        // Load commands.
        CommandManager.load();

        final PluginManager manager = getServer().getPluginManager();

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
                new CrateEditorListener(),
                new CrateOpenListener(),

                new PaperInteractListener()
        ).forEach(listener -> manager.registerEvents(listener, this));

        if (Support.nexo.isEnabled()) { // check for nexo
            manager.registerEvents(new NexoInteractListener(), this);
        }

        if (Support.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) getComponentLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) {
            // Print dependency garbage
            for (final Support value : Support.values()) {
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

    @ApiStatus.Internal
    public final InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @ApiStatus.Internal
    public final BukkitUserManager getUserManager() {
        return this.userManager;
    }

    @ApiStatus.Internal
    public final CrateManager getCrateManager() {
        return this.crateManager;
    }

    @ApiStatus.Internal
    public @Nullable final HeadDatabaseAPI getApi() {
        if (this.api == null) {
            return null;
        }

        return this.api;
    }

    @ApiStatus.Internal
    public final Server getInstance() {
        return this.instance;
    }

    @ApiStatus.Internal
    public @Nullable final MetricsWrapper getMetrics() {
        return this.metrics;
    }

    @ApiStatus.Internal
    public final VitalPaper getVital() {
        return this.vital;
    }

    @ApiStatus.Internal
    public final Timer getTimer() {
        return this.timer;
    }
}