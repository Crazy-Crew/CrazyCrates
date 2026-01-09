package us.crazycrew.crazycrates;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.IServer;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 0.7.0
 * @since 0.4.0
 */
public class CratesProvider {

    private static IServer instance;

    /**
     * Gets the instance of IServer which lets you interact with our plugin.
     *
     * @return {@link IServer}
     * @since 0.4.0
     */
    public static IServer get() {
        if (instance == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return instance;
    }

    @ApiStatus.Internal
    private CratesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Creates {@link IServer} instance.
     *
     * @param instance the {@link IServer} instance
     * @since 0.4.0
     */
    @ApiStatus.Internal
    public static void register(@NotNull final IServer instance) {
        if (CratesProvider.instance != null) return;

        CratesProvider.instance = instance;
    }

    /**
     * Unregisters {@link IServer} instance.
     *
     * @since 0.4.0
     */
    @ApiStatus.Internal
    public static void unregister() {
        CratesProvider.instance = null;
    }
}