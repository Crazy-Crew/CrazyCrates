package us.crazycrew.crazycrates.platform;

import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * A class containing available methods to use.
 *
 * @author Ryder Belserion
 * @version 0.7
 * @since 0.5
 */
public interface IServer {

    /**
     * Reloads the plugin
     *
     * @since 0.5
     */
    void reload();

    /**
     * Gets the crates path.
     *
     * @return {@link Path}
     * @since 0.9
     */
    @NotNull Path getCratesPath();

    /**
     * Gets the plugin's home folder
     *
     * @return {@link Path}
     * @since 0.9
     */
    @NotNull Path getDataPath();

    /**
     * Gets the plugin's crates folder.
     *
     * @return {@link File}
     * @since 0.5
     */
    @Deprecated(since = "0.9", forRemoval = true)
    @NotNull
    default File getCrateFolder() {
        return getCratesPath().toFile();
    }

    /**
     * Gets the plugin's data folder.
     *
     * @return {@link File}
     * @since 0.8
     */
    @Deprecated(since = "0.9", forRemoval = true)
    @NotNull
    default File getDataFolder() {
        return getDataPath().toFile();
    }

    /**
     * Gets a list of crate files with the option to keep or remove the extension.
     *
     * @param keepExtension true or false
     * @return {@link List<String>}
     * @since 0.9
     */
    List<String> getCrateFiles(final boolean keepExtension);

    /**
     * Gets a list of crate files without the extension.
     *
     * @return {@link List<String>}
     * @since 0.5
     */
    List<String> getCrateFiles();

    /**
     * A getter for the UserManager that handles fetching users, checking virtual keys, adding virtual keys or physical keys
     * Ability to set keys, get keys, getting total keys or checking total crates opened or individual crates opened.
     *
     * @return {@link UserManager}
     * @since 0.5
     */
    @NotNull UserManager getUserManager();

    /**
     * Gets available config options in a friendly way.
     *
     * @return {@link ISettings}
     * @since 0.5
     */
    @NotNull ISettings getSettings();

}