package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class CrazyCratesService {

    private static CrazyCrates plugin = null;

    public @NotNull CrazyCrates get() {
        CrazyCrates instance = CrazyCratesService.plugin;

        if (instance == null) {
            throw new RuntimeException("CrazyCrates service method not set. Please call the method setService before you try to use it!");
        }

        return plugin;
    }

    @ApiStatus.Internal
    private CrazyCratesService() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @ApiStatus.Internal
    public void setService(CrazyCrates plugin) {
        if (CrazyCratesService.plugin != null) return;

        CrazyCratesService.plugin = plugin;
    }

    @ApiStatus.Internal
    public void stopService() {
        if (CrazyCratesService.plugin == null) return;

        CrazyCratesService.plugin = null;
    }
}