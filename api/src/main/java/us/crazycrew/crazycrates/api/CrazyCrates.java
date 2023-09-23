package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.platforms.Platform;
import java.io.File;

public interface CrazyCrates {

    @NotNull Platform.type getPlatform();

    @NotNull File getDataFolder();

}