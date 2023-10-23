package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class CrazyCratesService {

    private static ICrazyCrates plugin = null;

    public static @NotNull ICrazyCrates get() {
        ICrazyCrates instance = CrazyCratesService.plugin;

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
    public static void setService(ICrazyCrates plugin) {
        if (CrazyCratesService.plugin != null) return;

        CrazyCratesService.plugin = plugin;
    }

    @ApiStatus.Internal
    public static void stopService() {
        if (CrazyCratesService.plugin == null) return;

        CrazyCratesService.plugin = null;
    }
}