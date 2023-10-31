package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.cratetypes.CSGO;
import com.badbones69.crazycrates.paper.cratetypes.Cosmic;
import com.badbones69.crazycrates.paper.cratetypes.CrateOnTheGo;
import com.badbones69.crazycrates.paper.cratetypes.QuadCrate;
import com.badbones69.crazycrates.paper.cratetypes.QuickCrate;
import com.badbones69.crazycrates.paper.cratetypes.Roulette;
import com.badbones69.crazycrates.paper.cratetypes.War;
import com.badbones69.crazycrates.paper.cratetypes.Wheel;
import com.badbones69.crazycrates.paper.cratetypes.Wonder;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import com.badbones69.crazycrates.paper.listeners.MenuListener;
import com.badbones69.crazycrates.paper.listeners.PreviewListener;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.support.placeholders.PlaceholderAPISupport;
import us.crazycrew.crazycrates.paper.listeners.MiscListener;
import us.crazycrew.crazycrates.paper.api.support.libraries.PluginSupport;
import us.crazycrew.crazycrates.paper.listeners.crates.CrateOpenListener;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.util.List;

public class CrazyCrates extends JavaPlugin {

    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private CrazyHandler crazyHandler;
    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        // Load version 2 of crazycrates
        this.crazyHandler = new CrazyHandler(getDataFolder());
        this.crazyHandler.load();

        // Clean if we have to.
        this.crazyHandler.cleanFiles();

        // Load crates temporarily here, This is leftovers from version 1.
        this.crazyManager = new CrazyManager();

        // Load deprecated version 1 to not break plugins that might use old api's
        new com.badbones69.crazycrates.paper.CrazyCrates().enable();

        // Register listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new MiscListener(), this);
        pluginManager.registerEvents(new MenuListener(), this);
        pluginManager.registerEvents(new PreviewListener(), this);
        pluginManager.registerEvents(new CrateControlListener(), this);
        pluginManager.registerEvents(new CrateOpenListener(), this);
        pluginManager.registerEvents(new MiscListener(), this);

        pluginManager.registerEvents(new War(), this);
        pluginManager.registerEvents(new CSGO(), this);
        pluginManager.registerEvents(new Wheel(), this);
        pluginManager.registerEvents(new Wonder(), this);
        pluginManager.registerEvents(new Cosmic(), this);
        pluginManager.registerEvents(new Roulette(), this);
        pluginManager.registerEvents(new QuickCrate(), this);
        pluginManager.registerEvents(new CrateOnTheGo(), this);
        pluginManager.registerEvents(new QuadCrate(), this);

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
                "Version: " + getDescription().getVersion() + " is currently backwards compatible.",
                "Please submit any bugs at https://github.com/Crazy-Crew/CrazyCrates/issues",
                "",
                "I will wait between releasing updates for bug reports",
                "The next versions after will come quickly such as Version 1.17, Version 1.18, Version 1.19 and Version 2.0",
                "1.17, 1.18 and 1.19 will likely have slight config migrations and overhauls in preparation for 2.0",
                "Everything will be auto-converted when possible but new features will also be included as well",
                "which you can find in changelogs when they release.... You can also submit feature requests as I am categorizing them",
                "",
                "2.0 is a hard break, Legacy color codes will no longer work, Configurations will be fully migrated, Placeholders will change and so on.",
                "We only support https://papermc.io in 2.0 and will fully migrate to Modrinth and Hangar.",
                "After that's done, I'll be adding practically anything including light gui editors or in-game editors ( improved /cc additem ) and crate conversions."
        ).forEach(getLogger()::warning);

        if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
            getLogger().info("PlaceholderAPI support is enabled!");
            new PlaceholderAPISupport().register();
        }
    }

    @Override
    public void onDisable() {
        // End all crates.
        SessionManager.endCrates();

        // Remove quick crate rewards
        QuickCrate.removeAllRewards();

        // Purge holograms.
        if (this.crazyHandler.getCrateManager().getHolograms() != null) this.crazyHandler.getCrateManager().getHolograms().removeAllHolograms();

        // Unload the plugin.
        this.crazyHandler.unload();
    }

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    //TODO() Migrate this to crazy handler
    @NotNull
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
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