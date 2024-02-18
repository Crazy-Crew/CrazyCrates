package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.MigrateManager;
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
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.other.quadcrates.SessionManager;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.api.FileManager;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.support.PluginSupport;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class CrazyCrates extends JavaPlugin {

    @NotNull
    public static CrazyCrates get() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    @NotNull
    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private CrazyHandler crazyHandler;
    private Timer timer;

    @Override
    public void onEnable() {
        // Migrate configurations.
        MigrateManager.migrate();

        this.timer = new Timer();

        registerPermissions();

        // Load version 2 of crazycrates
        this.crazyHandler = new CrazyHandler(this);
        this.crazyHandler.load();

        // Clean if we have to.
        this.crazyHandler.cleanFiles();

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
            String prefix = this.crazyHandler.getConfigManager().getConfig().getProperty(ConfigKeys.console_prefix);

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

        if (this.crazyHandler != null) {
            // Remove quick crate rewards
            this.crazyHandler.getCrateManager().purgeRewards();

            // Purge holograms.
            if (this.crazyHandler.getCrateManager().getHolograms() != null) this.crazyHandler.getCrateManager().getHolograms().removeAllHolograms();

            // Unload the plugin.
            this.crazyHandler.unload();
        }

        if (this.timer != null) this.timer.cancel();
    }

    @NotNull
    public BukkitCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }

    @NotNull
    public BukkitUserManager getUserManager() {
        return getCrazyHandler().getUserManager();
    }

    @NotNull
    public ConfigManager getConfigManager() {
        return getCrazyHandler().getConfigManager();
    }

    @NotNull
    public CrateManager getCrateManager() {
        return getCrazyHandler().getCrateManager();
    }

    @NotNull
    public FileManager getFileManager() {
        return getCrazyHandler().getFileManager();
    }

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    @NotNull
    public Timer getTimer() {
        return this.timer;
    }

    public boolean isLogging() {
        return getConfigManager().getConfig().getProperty(ConfigKeys.verbose_logging);
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
}