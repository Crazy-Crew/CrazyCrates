package us.crazycrew.crazycrates.platform;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.io.File;
import java.nio.file.Path;

/**
 * A class containing available methods to use.
 *
 * @author Ryder Belserion
 * @version 0.5
 */
public interface IServer {

    /**
     * Reloads the plugin
     */
    void reload();

    /**
     * @return the plugin directory
     */
    @NotNull Path getDirectory();

    /**
     * @return the crates folder
     */
    @NotNull File getCrateFolder();

    /**
     * @return the array of files in the crates folder
     */
    @NotNull File[] getCrateFiles();

    /**
     * @return the user manager
     */
    @NotNull UserManager getUserManager();

    /**
     * @return available config options
     */
    @NotNull ISettings getSettings();

}