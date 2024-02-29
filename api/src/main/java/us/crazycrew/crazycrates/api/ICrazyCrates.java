package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;

/**
 * An interface used to enable api related functions.
 *
 * @author Ryder Belserion
 * @version 0.3
 */
public interface ICrazyCrates {

    /**
     * Enables the api service.
     */
    void enable();

    /**
     * Disables the api service.
     */
    void disable();

    /**
     * @return the user manager
     */
    @NotNull UserManager getUserManager();

}