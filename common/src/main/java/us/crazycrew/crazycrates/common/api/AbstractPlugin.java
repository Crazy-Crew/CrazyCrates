package us.crazycrew.crazycrates.common.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import java.io.File;

public abstract class AbstractPlugin implements ICrazyCrates {

    @NotNull
    public abstract ConfigManager getConfigManager();

    private final Platform.type platform;
    private final File dataFolder;

    public AbstractPlugin(File dataFolder, Platform.type platform) {
        this.dataFolder = dataFolder;
        this.platform = platform;
    }

    public void enablePlugin() {
        CrazyCratesService.setService(this);
    }

    public void disablePlugin() {
        CrazyCratesService.stopService();
    }

    @NotNull
    @Override
    public Platform.type getPlatform() {
        return this.platform;
    }

    @NotNull
    @Override
    public File getDataFolder() {
        return this.dataFolder;
    }
}