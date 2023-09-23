package us.crazycrew.crazycrates.common;

import us.crazycrew.crazycrates.common.api.AbstractPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import java.io.File;

public class CrazyCratesPlugin extends AbstractPlugin {

    private final ConfigManager configManager;

    public CrazyCratesPlugin(File dataFolder) {
        this.configManager = new ConfigManager(dataFolder);
    }

    public void enable() {
        this.configManager.load();
    }

    public void disable() {
        this.configManager.reload();
    }

    @Override
    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}