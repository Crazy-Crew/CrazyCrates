package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.hooks.HeadDatabaseListener;
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
import com.badbones69.crazycrates.listeners.other.EntityDamageListener;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.VitalPaper;
import com.ryderbelserion.vital.enums.Support;
import com.ryderbelserion.vital.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.Server;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.util.List;
import java.util.Timer;
import static com.badbones69.crazycrates.api.utils.MiscUtils.registerPermissions;

public class CrazyCrates extends JavaPlugin {

    private final Timer timer;

    public CrazyCrates() {
        // Create timer object.
        this.timer = new Timer();
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;

    private Server instance;

    @Override
    public void onLoad() {
        this.instance = new Server(this);
        this.instance.enable();
    }

    @Override
    public void onEnable() {
        new VitalPaper(this);

        // Register permissions that we need.
        registerPermissions();

        // Register head database listener if the plugin enabled.
        if (Support.head_database.isEnabled()) {
            getServer().getPluginManager().registerEvents(new HeadDatabaseListener(), this);
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

        List.of(
                // Menu listeners.
                new CratePreviewMenu.CratePreviewListener(),
                new CrateAdminMenu.CrateAdminListener(),
                new CrateMainMenu.CrateMenuListener(),
                new CrateTierMenu.CrateTierListener(),

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

        if (MiscUtils.isLogging()) {
            final String prefix = ConfigManager.getConfig().getProperty(ConfigKeys.console_prefix);

            // Print dependency garbage
            for (final Support value : Support.values()) {
                if (value.isEnabled()) {
                    getServer().getConsoleSender().sendRichMessage(prefix + "<bold><gold>" + value.getName() + " <green>FOUND");
                } else {
                    getServer().getConsoleSender().sendRichMessage(prefix + "<bold><gold>" + value.getName() + " <red>NOT FOUND");
                }
            }
        }

        if (Support.placeholder_api.isEnabled()) {
            if (MiscUtils.isLogging()) getLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) getLogger().info("You can disable logging by going to the plugin-config.yml and setting verbose to false.");
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

            if (holograms != null && !holograms.isEmpty()) {
                holograms.removeAllHolograms(true);
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

    public @NotNull final FileManager getFileManager() {
        return this.instance.getFileManager();
    }

    public @NotNull final Server getInstance() {
        return this.instance;
    }

    public @NotNull final Timer getTimer() {
        return this.timer;
    }
}