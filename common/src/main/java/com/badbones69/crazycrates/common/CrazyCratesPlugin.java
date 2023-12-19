package com.badbones69.crazycrates.common;

import com.badbones69.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import java.io.File;

public abstract class CrazyCratesPlugin implements ICrazyCrates {

    private final File dataFolder;

    public CrazyCratesPlugin(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private ConfigManager configManager;

    @Override
    public void enable() {
        CrazyCratesService.setService(this);

        this.configManager = new ConfigManager(this.dataFolder);
        this.configManager.load();
    }

    @Override
    public void disable() {
        CrazyCratesService.stopService();

        this.configManager.reload();
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }
}