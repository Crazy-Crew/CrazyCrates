package us.crazycrew.crazycrates.common;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.api.AbstractPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import java.io.File;

public abstract class CrazyCratesPlugin extends AbstractPlugin {

    private ConfigManager configManager;

    public CrazyCratesPlugin(File dataFolder, Platform.type platform) {
        super(dataFolder, platform);
    }

    public void enable() {
        super.enablePlugin();

        this.configManager = new ConfigManager(getDataFolder());
        this.configManager.load();

        super.apiWasLoadedByOurPlugin();
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