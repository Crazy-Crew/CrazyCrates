package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.utils.FileUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
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
import com.badbones69.crazycrates.platform.PaperServer;
import com.badbones69.crazycrates.support.PluginSupport;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.support.metrics.MetricsManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.MigrationManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.badbones69.crazycrates.api.FileManager;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CrazyCrates;
import us.crazycrew.crazycrates.platform.impl.ConfigKeys;
import java.util.List;
import java.util.Timer;
import static com.badbones69.crazycrates.api.utils.MiscUtils.registerPermissions;

public class CrazyCratesPaper extends JavaPlugin {

    private CrazyCrates instance;

    @NotNull
    public static CrazyCratesPaper get() {
        return JavaPlugin.getPlugin(CrazyCratesPaper.class);
    }

    @NotNull
    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private final Timer timer;

    public CrazyCratesPaper() {
        // Create timer object.
        this.timer = new Timer();
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private FileManager fileManager;

    private MetricsManager metrics;

    @Override
    public void onEnable() {
        // Migrate as early as possible.
        MigrationManager.migrate();

        // Register permissions that we need.
        registerPermissions();

        // Initialize api and all configurations.
        PaperServer server = new PaperServer();

        this.instance = new CrazyCrates(server);

        // Bind file manager to the getter in server.
        this.fileManager = server.getFileManager();

        // Register files.
        this.fileManager.registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("WarCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CasinoExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                .registerCustomFilesFolder("/crates")
                .registerCustomFilesFolder("/schematics")
                .setup();

        // Set variables
        this.inventoryManager = server.getInventoryManager();
        this.crateManager = server.getCrateManager();

        // Load holograms.
        this.crateManager.loadHolograms();

        // Load example files.
        FileUtils.loadFiles();

        // Load the buttons.
        this.inventoryManager.loadButtons();

        // Load the crates.
        this.crateManager.loadCrates();

        this.userManager = server.getUserManager();

        // Load commands.
        CommandManager commandManager = new CommandManager();
        commandManager.load();

        // Load metrics.
        boolean metrics = this.instance.getConfig().getProperty(ConfigKeys.toggle_metrics);

        this.metrics = new MetricsManager();
        if (metrics) this.metrics.start();

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
            String prefix = this.instance.getConfig().getProperty(ConfigKeys.console_prefix);

            // Print dependency garbage
            for (PluginSupport value : PluginSupport.values()) {
                if (value.isPluginEnabled()) {
                    getServer().getConsoleSender().sendMessage(MsgUtils.color(prefix + "&6&l" + value.name() + " &a&lFOUND"));
                } else {
                    getServer().getConsoleSender().sendMessage(MsgUtils.color(prefix + "&6&l" + value.name() + " &c&lNOT FOUND"));
                }
            }
        }

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
            if (MiscUtils.isLogging()) getLogger().info("PlaceholderAPI support is enabled!");

            new PlaceholderAPISupport().register();
        }

        if (MiscUtils.isLogging()) getLogger().info("You can disable logging by going to the plugin-config.yml and setting verbose to false.");
    }

    @Override
    public void onDisable() {
        // Cancel the timer task.
        if (this.timer != null) this.timer.cancel();

        // Clean up any mess we may have left behind.
        if (this.crateManager != null) {
            this.crateManager.purgeRewards();

            HologramManager holograms = this.crateManager.getHolograms();

            if (holograms != null && !holograms.isMapEmpty()) {
                holograms.removeAllHolograms();
            }
        }

        // Disable api
        if (this.instance != null) this.instance.disable();
    }

    @NotNull
    public BukkitCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }

    @NotNull
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    @NotNull
    public CrateManager getCrateManager() {
        return this.crateManager;
    }

    @NotNull
    public CrazyCrates getCrazyCrates() {
        return this.instance;
    }

    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @NotNull
    public MetricsManager getMetrics() {
        return this.metrics;
    }

    @NotNull
    public Timer getTimer() {
        return this.timer;
    }
}