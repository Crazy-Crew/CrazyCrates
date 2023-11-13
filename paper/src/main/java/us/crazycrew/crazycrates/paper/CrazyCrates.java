package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.paper.listeners.crates.WarCrateListener;
import us.crazycrew.crazycrates.paper.managers.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.cratetypes.Cosmic;
import com.badbones69.crazycrates.paper.cratetypes.CrateOnTheGo;
import com.badbones69.crazycrates.paper.cratetypes.QuadCrate;
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
import us.crazycrew.crazycrates.paper.api.modules.ModuleLoader;
import us.crazycrew.crazycrates.paper.listeners.crates.CrateOpenListener;
import us.crazycrew.crazycrates.paper.listeners.menus.CrateAdminListener;
import us.crazycrew.crazycrates.paper.other.MsgUtils;
import java.util.List;

public class CrazyCrates extends JavaPlugin {

    @NotNull
    public static CrazyCrates get() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    @NotNull
    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private CrazyHandler crazyHandler;
    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        // Load version 2 of crazycrates
        this.crazyHandler = new CrazyHandler(this);
        this.crazyHandler.load();

        // Clean if we have to.
        this.crazyHandler.cleanFiles();

        // Load crates temporarily here, This is leftovers from version 1.
        this.crazyManager = new CrazyManager();

        // Load deprecated version 1 to not break plugins that might use old api's
        new com.badbones69.crazycrates.paper.CrazyCrates().enable();

        // Register listeners
        //this.crazyHandler.getModuleLoader().addModule(new CrateGuiListener());
        this.crazyHandler.getModuleLoader().addModule(new CratePreviewListener());
        this.crazyHandler.getModuleLoader().addModule(new CrateAdminListener());
        this.crazyHandler.getModuleLoader().addModule(new CrateMenuListener());

        this.crazyHandler.getModuleLoader().load();

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MiscListener(), this);
        pluginManager.registerEvents(new CrateControlListener(), this);
        pluginManager.registerEvents(new CrateOpenListener(), this);

        pluginManager.registerEvents(new WarCrateListener(), this);

        pluginManager.registerEvents(new Cosmic(), this);
        pluginManager.registerEvents(new CrateOnTheGo(), this);
        pluginManager.registerEvents(new QuadCrate(), this);

        if (isLogging()) {
            // Print dependency garbage
            for (PluginSupport value : PluginSupport.values()) {
                if (value.isPluginEnabled()) {
                    getServer().getConsoleSender().sendMessage(MsgUtils.color(this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.command_prefix) + "&6&l" + value.name() + " &a&lFOUND"));
                } else {
                    getServer().getConsoleSender().sendMessage(MsgUtils.color(this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.command_prefix) + "&6&l" + value.name() + " &c&lNOT FOUND"));
                }
            }

            List.of(
                    "CrazyCrate Update: " + getDescription().getVersion() + " is one of 4 major updates.",
                    "Please submit any bugs at https://github.com/Crazy-Crew/CrazyCrates/issues",
                    "",
                    "I will wait between releasing updates for bug reports",
                    "The next version is Version 2.0 excluding version bumps for Minecraft releases or some easy features/enhancements",
                    "",
                    "2.0 is a hard break, Legacy color codes will no longer work, Configurations will be fully migrated, Placeholders will change and so on.",
                    "We only support https://papermc.io in 2.0 and will fully migrate to Modrinth and Hangar.",
                    "After that's done, I'll be adding practically anything including light gui editors or in-game editors ( improved /cc additem ) and crate conversions."
            ).forEach(getLogger()::warning);
        }

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
            if (isLogging()) getLogger().info("PlaceholderAPI support is enabled!");
            new PlaceholderAPISupport().register();
        }
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
    }

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    // TODO() Remove this in 2.0
    @NotNull
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    @NotNull
    public ModuleLoader getModuleLoader() {
        return this.crazyHandler.getModuleLoader();
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
}