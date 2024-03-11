package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.listeners.BrokeLocationsListener;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.crates.*;
import com.badbones69.crazycrates.listeners.other.EntityDamageListener;
import com.badbones69.crazycrates.platform.PaperServer;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.CrazyManager;
import com.badbones69.crazycrates.tasks.MigrationManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.SessionManager;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.platform.impl.ConfigKeys;
import com.badbones69.crazycrates.api.FileManager;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.support.PluginSupport;
import us.crazycrew.crazycrates.CrazyCrates;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class CrazyCratesPaper extends JavaPlugin {

    private CrazyCrates crazyCrates;

    @NotNull
    public static CrazyCratesPaper get() {
        return JavaPlugin.getPlugin(CrazyCratesPaper.class);
    }

    @NotNull
    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private CrazyManager crazyManager;
    private Timer timer;

    @Override
    public void onEnable() {
        // Migrate as early as possible.
        MigrationManager.migrate();

        // Initialize api.
        this.crazyCrates = new CrazyCrates(new PaperServer());

        // Create timer object.
        this.timer = new Timer();

        // Register permissions.
        registerPermissions();

        // Load version 2 of crazycrates
        this.crazyManager = new CrazyManager();
        this.crazyManager.load();

        // Clean if we have to.
        this.crazyManager.cleanFiles();

        // Register listeners
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

        if (isLogging()) {
            String prefix = this.crazyCrates.getConfig().getProperty(ConfigKeys.console_prefix);

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
            if (isLogging()) getLogger().info("PlaceholderAPI support is enabled!");
            new PlaceholderAPISupport().register();
        }

        if (isLogging()) getLogger().info("You can disable logging by going to the plugin-config.yml and setting verbose to false.");
    }

    @Override
    public void onDisable() {
        // End all crates.
        SessionManager.endCrates();

        if (this.crazyManager != null) {
            CrateManager crateManager = this.crazyManager.getCrateManager();

            // Remove quick crate rewards
            crateManager.purgeRewards();

            // Purge holograms.
            if (crateManager.getHolograms() != null) crateManager.getHolograms().removeAllHolograms();
        }

        if (this.timer != null) this.timer.cancel();

        if (this.crazyCrates != null) this.crazyCrates.disable();
    }

    @NotNull
    public BukkitCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }

    @NotNull
    public BukkitUserManager getUserManager() {
        return getCrazyManager().getUserManager();
    }

    @NotNull
    public CrateManager getCrateManager() {
        return getCrazyManager().getCrateManager();
    }

    @NotNull
    public FileManager getFileManager() {
        return getCrazyManager().getFileManager();
    }

    @NotNull
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    @NotNull
    public Timer getTimer() {
        return this.timer;
    }

    public boolean isLogging() {
        return this.crazyCrates.getConfig().getProperty(ConfigKeys.verbose_logging);
    }

    private void registerPermissions() {
        Arrays.stream(Permissions.values()).toList().forEach(permission -> {
            Permission newPermission = new Permission(
                    permission.getPermission(),
                    permission.getDescription(),
                    permission.isDefault(),
                    permission.getChildren()
            );

            getServer().getPluginManager().addPermission(newPermission);
        });
    }

    @NotNull
    public CrazyCrates getCrazyCrates() {
        return this.crazyCrates;
    }
}