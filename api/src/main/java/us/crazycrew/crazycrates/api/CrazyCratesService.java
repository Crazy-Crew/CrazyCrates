package us.crazycrew.crazycrates.api;

@Deprecated(forRemoval = true, since = "0.4")
public class CrazyCratesService {

    @Deprecated(forRemoval = true, since = "0.4")
    public static ICrazyCrates get() {
        if (CrazyProvider.get() == null) {
            throw new IllegalStateException("CrazyCrates API is not loaded.");
        }

        return CrazyProvider.get();
    }
}