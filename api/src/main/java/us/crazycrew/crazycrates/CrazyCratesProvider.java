package us.crazycrew.crazycrates;

import com.ryderbelserion.vital.common.util.StringUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus;
import java.util.List;

public final class CrazyCratesProvider {

    private static CrazyCratesApi instance = null;

    public static @NonNull CrazyCratesApi get() {
        CrazyCratesApi instance = CrazyCratesProvider.instance;

        if (instance == null) {
            throw new UnavailableException();
        }

        return instance;
    }

    @ApiStatus.Internal
    public static void register(CrazyCratesApi instance) {
        CrazyCratesProvider.instance = instance;
    }

    @ApiStatus.Internal
    public static void unregister() {
        CrazyCratesProvider.instance = null;
    }

    private static final class UnavailableException extends IllegalStateException {

        private static final List<String> message = List.of(
                "The CrazyCrates API isn't loaded yet.",
                "",
                "A list of potential reasons:",
                " 1) CrazyCrates plugin did not enable, or is not installed.",
                " 2) The plugin using CrazyCrates does not include it as a softdepend.",
                " 3) The plugin is trying to use the api before the api enables."
        );

        UnavailableException() {
            super(StringUtil.convertList(message));
        }
    }
}