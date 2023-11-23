package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.EventLogger;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.paper.listeners.platforms.PaperListener;
import us.crazycrew.crazycrates.paper.listeners.crates.WarCrateListener;
import us.crazycrew.crazycrates.paper.listeners.platforms.SpigotListener;
import us.crazycrew.crazycrates.paper.managers.BukkitUserManager;
import us.crazycrew.crazycrates.paper.managers.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import us.crazycrew.crazycrates.paper.managers.crates.other.quadcrates.SessionManager;
import us.crazycrew.crazycrates.paper.listeners.crates.CosmicCrateListener;
import us.crazycrew.crazycrates.paper.listeners.crates.MobileCrateListener;
import us.crazycrew.crazycrates.paper.listeners.crates.QuadCrateListener;
import us.crazycrew.crazycrates.paper.listeners.CrateControlListener;
import us.crazycrew.crazycrates.paper.listeners.menus.CrateMenuListener;
import us.crazycrew.crazycrates.paper.listeners.menus.CratePreviewListener;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.support.placeholders.PlaceholderAPISupport;
import us.crazycrew.crazycrates.paper.listeners.MiscListener;
import us.crazycrew.crazycrates.paper.api.support.libraries.PluginSupport;
import us.crazycrew.crazycrates.paper.listeners.crates.CrateOpenListener;
import us.crazycrew.crazycrates.paper.listeners.menus.CrateAdminListener;
import us.crazycrew.crazycrates.paper.other.MsgUtils;
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
            String prefix = this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.console_prefix);

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
        return getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }

    public boolean isSpigot() {
        return this.isSpigot;
    }
}