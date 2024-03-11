package us.crazycrew.crazycrates;

/**
 * A class used to initialize the api so other plugins can use it.
 *
 * @author Ryder Belserion
 * @version 0.4
 */
public class CrazyCratesProvider {

    private static CrazyCrates instance;

    private CrazyCratesProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static CrazyCrates get() {
        if (instance == null) {
            throw new IllegalStateException("CrazyCrates is not loaded.");
        }

        return instance;
    }

    static void register(CrazyCrates instance) {
        if (CrazyCratesProvider.instance == null) {
            return;
        }

        CrazyCratesProvider.instance = instance;
    }

    static void unregister() {
        CrazyCratesProvider.instance = null;
    }
}