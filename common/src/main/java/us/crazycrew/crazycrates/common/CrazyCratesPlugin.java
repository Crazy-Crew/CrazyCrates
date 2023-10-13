package us.crazycrew.crazycrates.common;

import com.ryderbelserion.cluster.api.RootPlugin;
import com.ryderbelserion.cluster.api.adventure.FancyLogger;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.api.AbstractPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import java.io.File;

public abstract class CrazyCratesPlugin extends AbstractPlugin {

    private ConfigManager configManager;

    public CrazyCratesPlugin(File dataFolder, Platform.type platform) {
        super(dataFolder, platform);
    }

    public void enable(Audience audience) {
        super.enablePlugin();

        this.configManager = new ConfigManager(getDataFolder());
        this.configManager.load();

        RootPlugin.setConsole(audience);
        FancyLogger.setName(this.configManager.getPluginConfig().getProperty(PluginConfig.console_prefix));
    }

    public void disable() {
        super.disablePlugin();

        this.configManager.reload();
    }

    @NotNull
    @Override
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}