package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.managers.quadcrates.SessionManager;
import com.badbones69.crazycrates.paper.cratetypes.QuickCrate;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import us.crazycrew.crazycrates.paper.support.libraries.PluginSupport;

public class CrazyCrates extends JavaPlugin {

    private final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(this);

    private com.badbones69.crazycrates.paper.CrazyCrates crazyCrates;

    private CrazyHandler crazyHandler;
    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        this.crazyCrates = new com.badbones69.crazycrates.paper.CrazyCrates();
        this.crazyCrates.enable();

        this.crazyHandler = new CrazyHandler(getDataFolder());
        this.crazyHandler.load();

        this.crazyManager = new CrazyManager();
        this.crazyManager.loadCrates();

        for (PluginSupport value : PluginSupport.values()) {
            if (value.isPluginEnabled()) {
                getLogger().info((this.crazyHandler.getMethods().color("&6&l" + value.name() + " &a&lFOUND")));
            } else {
                getLogger().info(this.crazyHandler.getMethods().color("&6&l" + value.name() + " &c&lNOT FOUND"));
            }
        }
    }

    @Override
    public void onDisable() {
        SessionManager.endCrates();

        QuickCrate.removeAllRewards();

        //TODO() Remove holograms.

        this.crazyHandler.unload();
    }

    @NotNull
    public com.badbones69.crazycrates.paper.CrazyCrates getCrazyCrates() {
        return this.crazyCrates;
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

    public boolean isLogging() {
        return getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }
}