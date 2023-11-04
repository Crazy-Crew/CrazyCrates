package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;

public interface ICrazyCrates {

    void enable();

    void disable();

    @NotNull UserManager getUserManager();

}