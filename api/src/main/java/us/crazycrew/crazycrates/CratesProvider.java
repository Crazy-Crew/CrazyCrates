package us.crazycrew.crazycrates;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.IServer;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 0.5
 * @since 0.4
 */
public class CratesProvider {

    private static IServer instance;

    /**
     * @return {@link IServer}
     * @since 0.4
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
     */
    @ApiStatus.Internal
    public static void register(@NotNull final IServer instance) {
        if (CratesProvider.instance != null) return;

        CratesProvider.instance = instance;
    }

    /**
     * Unregisters {@link IServer} instance.
     */
    @ApiStatus.Internal
    public static void unregister() {
        CratesProvider.instance = null;
    }
}