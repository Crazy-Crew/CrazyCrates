package us.crazycrew.crazycrates.common;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import java.io.File;

public abstract class CrazyCratesPlugin implements ICrazyCrates {

    private final File dataFolder;

    public CrazyCratesPlugin(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void enable() {
        CrazyCratesService.setService(this);
    }

    @Override
    public void disable() {
        CrazyCratesService.stopService();
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.dataFolder;
    }
}