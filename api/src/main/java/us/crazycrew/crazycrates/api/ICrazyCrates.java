package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;

/**
 * An interface used to enable api related functions.
 *
 * @author Ryder Belserion
 * @version 1.0-snapshot
 */
@Deprecated(forRemoval = true)
public interface ICrazyCrates {

    /**
     * @return the user manager
     */
    @NotNull UserManager getUserManager();

}