package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.objects.other.Server;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.CommandGui;
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
import com.badbones69.crazycrates.support.MetricsWrapper;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ryderbelserion.vital.paper.Vital;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import static com.badbones69.crazycrates.api.utils.MiscUtils.registerPermissions;

public class CrazyCrates extends Vital {

    public static CrazyCrates getPlugin() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    private final Timer timer;
    private final long startTime;

    public CrazyCrates() {
        this.startTime = System.nanoTime();

        // Create timer object.
        this.timer = new Timer();
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private HeadDatabaseAPI api;

    private Server instance;

    @Override
    public void onEnable() {
        this.instance = new Server(getDataFolder());
        this.instance.apply();

        getFileManager().addFile(new File(getDataFolder(), "locations.yml")).addFile(new File(getDataFolder(),"data.yml"))
                .addFolder("crates")
                .addFolder("schematics")
                .init();

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

        // Load commands.
        CommandManager.load();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            LiteralArgumentBuilder<CommandSourceStack> root = new CommandGui().registerPermission().literal().createBuilder();

            event.registrar().register(root.build(), "the base command for Vital");
        });

        new MetricsWrapper(this, 4514).start();

        List.of(
                // Other listeners.
                new BrokeLocationsListener(),
                new CrateControlListener(),
                new EntityDamageListener(),
                new MobileCrateListener(),
                new CosmicCrateListener(),
                new QuadCrateListener(),
                new CrateOpenListener(),
                new WarCrateListener(),
                new MiscListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        if (Support.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) getComponentLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) {
            // Print dependency garbage
            for (final Support value : Support.values()) {
                if (value.isEnabled()) {
                    getComponentLogger().info(AdvUtil.parse("<bold><gold>" + value.getName() + " <green>FOUND"));
                } else {
                    getComponentLogger().info(AdvUtil.parse("<bold><gold>" + value.getName() + " <red>NOT FOUND"));
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
    }

    public @NotNull final InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public @NotNull final BukkitUserManager getUserManager() {
        return this.userManager;
    }

    public @NotNull final CrateManager getCrateManager() {
        return this.crateManager;
    }

    public @Nullable final HeadDatabaseAPI getApi() {
        if (this.api == null) {
            return null;
        }

        return this.api;
    }

    public @NotNull final Server getInstance() {
        return this.instance;
    }

    public @NotNull final Timer getTimer() {
        return this.timer;
    }
}