package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 0.3
 */
public class CrazyCratesProvider {

    private static ICrazyCrates instance = null;

    public static ICrazyCrates get() {
        if (instance == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return instance;
    }

    @ApiStatus.Internal
    private CrazyCratesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @ApiStatus.Internal
    public static void register(final ICrazyCrates instance) {
        if (CrazyCratesProvider.instance != null) {
            return;
        }

        CrazyCratesProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        CrazyCratesProvider.instance = null;
    }
}