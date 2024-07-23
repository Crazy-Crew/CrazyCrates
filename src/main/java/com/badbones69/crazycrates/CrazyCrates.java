package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePrizeMenu;
import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.tasks.PaginationManager;
import com.badbones69.crazycrates.api.objects.other.Server;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.CommandManager;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.crates.CosmicCrateListener;
import com.badbones69.crazycrates.listeners.crates.CrateOpenListener;
import com.badbones69.crazycrates.listeners.crates.MobileCrateListener;
import com.badbones69.crazycrates.listeners.crates.QuadCrateListener;
import com.badbones69.crazycrates.listeners.crates.WarCrateListener;
import com.badbones69.crazycrates.support.MetricsWrapper;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.VitalPaper;
import com.ryderbelserion.vital.paper.enums.Support;
import com.ryderbelserion.vital.paper.files.config.FileManager;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import static com.badbones69.crazycrates.api.utils.MiscUtils.registerPermissions;

public class CrazyCrates extends JavaPlugin {

    private final Timer timer;
    private final long startTime;

    public CrazyCrates() {
        this.startTime = System.nanoTime();

        // Create timer object.
        this.timer = new Timer();
    }

    private PaginationManager paginationManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private FileManager fileManager;
    private HeadDatabaseAPI api;

    private Server instance;

    @Override
    public void onEnable() {
        new VitalPaper(this);

        this.instance = new Server(getDataFolder());
        this.instance.apply();

        this.fileManager = new FileManager();
        this.fileManager.addFile("locations.yml").addFile("data.yml")
                .addFolder("crates")
                .addFolder("schematics")
                .init();

        // Register permissions that we need.
        registerPermissions();

        if (Support.head_database.isEnabled()) {
            this.api = new HeadDatabaseAPI();
        }

        this.paginationManager = new PaginationManager();
        this.crateManager = new CrateManager();
        this.userManager = new BukkitUserManager();

        this.instance.setUserManager(this.userManager);

        // Load holograms.
        this.crateManager.loadHolograms();

        // Load the buttons.
        this.paginationManager.loadButtons();

        // Load the crates.
        this.crateManager.loadCrates();

        // Load commands.
        CommandManager.load();

        new MetricsWrapper(this, 4514).start();

        List.of(
                // Menu listeners.
                new CratePreviewMenu(),
                new CrateAdminMenu(),
                new CratePrizeMenu(),
                new CrateMainMenu(),
                new CrateTierMenu(),

                // Other listeners.
                new BrokeLocationsListener(),
                new CrateControlListener(),
                new MobileCrateListener(),
                new CosmicCrateListener(),
                new QuadCrateListener(),
                new CrateOpenListener(),
                new WarCrateListener(),
                new MiscListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        if (MiscUtils.isLogging()) {
            // Print dependency garbage
            for (final Support value : Support.values()) {
                if (value.isEnabled()) {
                    getComponentLogger().info(AdvUtil.parse("<bold><gold>" + value.getName() + " <green>FOUND"));
                } else {
                    getComponentLogger().info(AdvUtil.parse("<bold><gold>" + value.getName() + " <red>NOT FOUND"));
                }
            }
        }

        if (Support.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) getComponentLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) {
            getComponentLogger().info("You can disable logging by going to the plugin-config.yml and setting verbose to false.");

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
    }

    public final PaginationManager getPaginationManager() {
        return this.paginationManager;
    }

    public final BukkitUserManager getUserManager() {
        return this.userManager;
    }

    public final CrateManager getCrateManager() {
        return this.crateManager;
    }

    public final FileManager getFileManager() {
        return this.fileManager;
    }

    public @Nullable final HeadDatabaseAPI getApi() {
        if (this.api == null) {
            return null;
        }

        return this.api;
    }

    public final Server getInstance() {
        return this.instance;
    }

    public final Timer getTimer() {
        return this.timer;
    }
}