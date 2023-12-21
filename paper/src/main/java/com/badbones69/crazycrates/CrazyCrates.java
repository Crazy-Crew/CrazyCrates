package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.EventLogger;
import com.badbones69.crazycrates.listeners.CrateControlListener;
import com.badbones69.crazycrates.listeners.MiscListener;
import com.badbones69.crazycrates.listeners.crates.*;
import com.badbones69.crazycrates.listeners.menus.CrateAdminListener;
import com.badbones69.crazycrates.listeners.menus.CrateMenuListener;
import com.badbones69.crazycrates.listeners.menus.CratePreviewListener;
import com.badbones69.crazycrates.listeners.platforms.PaperListener;
import com.badbones69.crazycrates.listeners.platforms.SpigotListener;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import com.badbones69.crazycrates.managers.crates.CrateManager;
import com.badbones69.crazycrates.managers.crates.other.quadcrates.SessionManager;
import com.badbones69.crazycrates.other.MsgUtils;
import org.bukkit.plugin.java.JavaPlugin;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.api.FileManager;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.support.placeholders.PlaceholderAPISupport;
import com.badbones69.crazycrates.support.libraries.PluginSupport;
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

    private boolean isSpigot;

    @Override
    public void onEnable() {
        this.timer = new Timer();

        // Load version 2 of crazycrates
        this.crazyHandler = new CrazyHandler(this);
        this.crazyHandler.load();

        // Clean if we have to.
        this.crazyHandler.cleanFiles();

        // Register listeners
        this.crazyHandler.getModuleLoader().addModule(new CratePreviewListener());
        this.crazyHandler.getModuleLoader().addModule(new CrateAdminListener());
        this.crazyHandler.getModuleLoader().addModule(new CrateMenuListener());

        this.crazyHandler.getModuleLoader().load();

        try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            this.isSpigot = false;
        } catch (ClassNotFoundException exception) {
            this.isSpigot = true;
        }

        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new CrateControlListener(), this);
        pluginManager.registerEvents(new MobileCrateListener(), this);
        pluginManager.registerEvents(new CosmicCrateListener(), this);
        pluginManager.registerEvents(new QuadCrateListener(), this);
        pluginManager.registerEvents(new CrateOpenListener(), this);
        pluginManager.registerEvents(new WarCrateListener(), this);
        pluginManager.registerEvents(new MiscListener(), this);

        if (this.isSpigot) pluginManager.registerEvents(new SpigotListener(), this); else pluginManager.registerEvents(new PaperListener(), this);

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

            if (this.isSpigot) {
                List.of(
                        "CrazyCrates will no longer work using Spigot when 2.0 releases",
                        "If you wish to keep using and getting updates for our plugin",
                        "You must migrate your servers to https://papermc.io",
                        "",
                        "This is a warning to all Spigot users",
                        "",
                        "All downloads will be served only at the following links per Spigot's rules",
                        "https://hangar.papermc.io/CrazyCrew/CrazyCrates",
                        "https://modrinth.com/plugin/crazycrates"
                ).forEach(getLogger()::warning);
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

        // Remove quick crate rewards
        this.crazyHandler.getCrateManager().purgeRewards();

        // Purge holograms.
        if (this.crazyHandler.getCrateManager().getHolograms() != null) this.crazyHandler.getCrateManager().getHolograms().removeAllHolograms();

        // Unload the plugin.
        this.crazyHandler.unload();

        if (this.timer != null) this.timer.cancel();
    }

    @NotNull
    public Timer getTimer() {
        return this.timer;
    }

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    @NotNull
    public ConfigManager getConfigManager() {
        return getCrazyHandler().getConfigManager();
    }

    @NotNull
    public FileManager getFileManager() {
        return getCrazyHandler().getFileManager();
    }

    @NotNull
    public BukkitUserManager getUserManager() {
        return getCrazyHandler().getUserManager();
    }

    @NotNull
    public CrateManager getCrateManager() {
        return getCrazyHandler().getCrateManager();
    }

    @NotNull
    public BukkitCommandManager<CommandSender> getCommandManager() {
        return this.commandManager;
    }

    @NotNull
    public EventLogger getEventLogger() {
        return getCrazyHandler().getEventLogger();
    }

    public boolean isLogging() {
        return getConfigManager().getConfig().getProperty(ConfigKeys.verbose_logging);
    }

    public boolean isSpigot() {
        return this.isSpigot;
    }
}