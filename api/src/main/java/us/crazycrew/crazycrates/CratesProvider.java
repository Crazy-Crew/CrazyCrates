package us.crazycrew.crazycrates;

import org.jetbrains.annotations.ApiStatus;
import us.crazycrew.crazycrates.platform.Server;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 1.0-snapshot
 */
public class CratesProvider {

    private static Server instance;

    public static Server get() {
        if (instance == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return instance;
    }

    @ApiStatus.Internal
    private CratesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @ApiStatus.Internal
    public static void register(Server instance) {
        if (CratesProvider.instance != null) return;

        CratesProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        CratesProvider.instance = null;
    }
}