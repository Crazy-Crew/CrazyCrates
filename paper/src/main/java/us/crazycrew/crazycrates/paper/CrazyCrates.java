package us.crazycrew.crazycrates.paper;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;

public class CrazyCrates extends JavaPlugin {

    private CrazyHandler crazyHandler;

    @Override
    public void onEnable() {
        this.crazyHandler = new CrazyHandler(this);
        this.crazyHandler.install();
    }

    @Override
    public void onDisable() {
        this.crazyHandler.uninstall();
    }

    @NotNull
    public CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    @NotNull
    public ConfigManager getConfigManager() {
        return this.crazyHandler.getConfigManager();
    }

    public boolean isLogging() {
        return this.crazyHandler.getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging);
    }
}