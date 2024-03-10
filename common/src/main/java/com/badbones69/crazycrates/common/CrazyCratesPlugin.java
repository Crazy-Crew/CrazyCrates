package com.badbones69.crazycrates.common;

import com.badbones69.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.api.CrazyProvider;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import java.io.File;

public abstract class CrazyCratesPlugin implements ICrazyCrates {

    private final File dataFolder;

    public CrazyCratesPlugin(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void enable() {
        // Load config files.
        ConfigManager.load(this.dataFolder);

        // Register provider.
        CrazyProvider.register(this);
    }

    @Override
    public void disable() {
        // Reload config files.
        ConfigManager.reload();

        // Unregister provider.
        CrazyProvider.unregister();
    }
}