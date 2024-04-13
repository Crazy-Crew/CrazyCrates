package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.ApiStatus;

@Deprecated(forRemoval = true)
public class CrazyCratesService {

    private static ICrazyCrates instance = null;

    public static ICrazyCrates get() {
        if (instance == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return instance;
    }

    @ApiStatus.Internal
    private CrazyCratesService() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @ApiStatus.Internal
    public static void register(final ICrazyCrates instance) {
        if (CrazyCratesService.instance != null) {
            return;
        }

        CrazyCratesService.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        CrazyCratesService.instance = null;
    }
}