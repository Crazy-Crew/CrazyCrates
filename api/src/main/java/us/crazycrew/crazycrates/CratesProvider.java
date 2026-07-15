package us.crazycrew.crazycrates;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.platform.IServer;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 1.0.0
 * @since 0.4.0
 */
public class CratesProvider {

    private static CrazyCrates crates;

    @ApiStatus.Internal
    private CratesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Gets the instance of IServer which lets you interact with our plugin.
     *
     * @return {@link CrazyCrates}
     * @since 0.4.0
     */
    public static CrazyCrates api() {
        if (crates == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return crates;
    }

    /**
     * Gets the instance of IServer which lets you interact with our plugin.
     *
     * @return {@link IServer}
     * @since 0.4.0
     */
    @Deprecated(forRemoval = true)
    public static IServer get() {
        if (crates == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return crates;
    }

    /**
     * Creates {@link CrazyCrates} instance.
     *
     * @param instance the {@link CrazyCrates} instance
     * @since 0.4.0
     */
    @ApiStatus.Internal
    public static void register(@NotNull final CrazyCrates instance) {
        if (CratesProvider.crates != null) return;

        CratesProvider.crates = instance;
    }

    /**
     * Unregisters {@link CrazyCrates} instance.
     *
     * @since 0.4.0
     */
    @ApiStatus.Internal
    public static void unregister() {
        CratesProvider.crates = null;
    }
}