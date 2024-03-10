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

    @Override
    public void enable() {
        CrazyCratesService.setService(this);

        ConfigManager.load(this.dataFolder);
    }

    @Override
    public void disable() {
        CrazyCratesService.stopService();

        ConfigManager.reload();
    }
}