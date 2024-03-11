package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;

/**
 * An interface used to enable api related functions.
 *
 * @author Ryder Belserion
 * @version 0.4
 */
@Deprecated(forRemoval = true, since = "0.4")
public interface ICrazyCrates {

    /**
     * @return the user manager
     */
    @NotNull UserManager getUserManager();

}