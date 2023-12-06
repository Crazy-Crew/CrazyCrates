package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 0.3
 */
public class CrazyCratesService {

    private static ICrazyCrates plugin = null;

    /**
     * Fetches the instance of the interface for api use.
     *
     * @return the instance of ICrazyCrates
     */
    public static @NotNull ICrazyCrates get() {
        ICrazyCrates instance = CrazyCratesService.plugin;

        if (instance == null) {
            throw new RuntimeException("CrazyCrates service method not set. Please call the method setService before you try to use it!");
        }

        return plugin;
    }

    /**
     * Prevents creating an instance of this class.
     */
    @ApiStatus.Internal
    private CrazyCratesService() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Creates the static instance of ICrazyCrates
     *
     * @param plugin instance to use
     */
    @ApiStatus.Internal
    public static void setService(ICrazyCrates plugin) {
        if (CrazyCratesService.plugin != null) return;

        CrazyCratesService.plugin = plugin;
    }

    /**
     * Stops the crazy crates service
     */
    @ApiStatus.Internal
    public static void stopService() {
        if (CrazyCratesService.plugin == null) return;

        CrazyCratesService.plugin = null;
    }
}