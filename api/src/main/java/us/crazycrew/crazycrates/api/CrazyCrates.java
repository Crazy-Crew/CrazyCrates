package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.api.users.UserManager;

import java.io.File;

public interface CrazyCrates {

    //@NotNull
    //UserManager getUserManager();

    @NotNull
    Platform.type getPlatform();

    @NotNull
    File getDataFolder();

}