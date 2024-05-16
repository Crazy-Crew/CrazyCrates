package us.crazycrew.crazycrates.platform;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.io.File;
import java.util.List;

/**
 * A class containing available methods to use.
 *
 * @author Ryder Belserion
 * @version 0.5
 * @since 0.5
 */
public interface IServer {

    /**
     * Reloads the plugin
     * @since 0.5
     */
    void reload();

    /**
     * @return {@link File}
     * @since 0.5
     */
    @NotNull File getCrateFolder();

    /**
     * @return {@link List<String>}
     * @since 0.5
     */
    List<String> getCrateFiles();

    /**
     * @return {@link UserManager}
     * @since 0.5
     */
    @NotNull UserManager getUserManager();

    /**
     * @return {@link ISettings}
     * @since 0.5
     */
    @NotNull ISettings getSettings();

}