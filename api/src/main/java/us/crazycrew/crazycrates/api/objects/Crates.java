package us.crazycrew.crazycrates.api.objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.KeyManager;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.IServer;
import us.crazycrew.crazycrates.platform.ISettings;
import java.nio.file.Path;
import java.util.List;

public abstract class Crates implements IServer {

    protected final Path path;

    public Crates(@NotNull final Path path) {
        this.path = path;
    }

    /**
     * Gets the crates path.
     *
     * @return {@link Path}
     * @since 1.0.0
     */
    public @NotNull Path getCratesPath() {
        return getDataPath().resolve("crates");
    }

    /**
     * Gets the plugin's home folder.
     *
     * @return {@link Path}
     * @since 1.0.0
     */
    public @NotNull Path getDataPath() {
        return this.path;
    }

    /**
     * Starts the plugin.
     *
     * @since 1.0.0
     */
    @ApiStatus.Internal
    public abstract void init();

    /**
     * Starts post plugin logic.
     *
     * @since 1.0.0
     */
    @ApiStatus.Internal
    public abstract void post();

    /**
     * Reloads the plugin.
     *
     * @since 1.0.0
     */
    public abstract void reload();

    /**
     * Stops the plugin.
     *
     * @since 1.0.0
     */
    @ApiStatus.Internal
    public abstract void stop();

    /**
     * A getter for the UserManager that handles fetching users, checking virtual keys, adding virtual keys or physical keys
     * Ability to set keys, get keys, getting total keys or checking total crates opened or individual crates opened.
     *
     * @return {@link UserManager}
     * @since 0.5.0
     */
    public abstract @NotNull UserManager getUserManager();

    /**
     * A getter for the KeyManager, It handles checking physical keys, and anything else we might need soon.
     *
     * @return {@link KeyManager}
     * @since 0.9.0
     */
    public abstract @NotNull KeyManager getKeyManager();

    /**
     * Gets available config options in a friendly way.
     *
     * @return {@link ISettings}
     * @since 0.5.0
     */
    public abstract @NotNull ISettings getSettings();

    /**
     * Fetches a list of files, from the crates directory.
     *
     * @param hasExtension true or false
     * @return list of files
     * @since 1.0.0
     */
    public abstract List<String> getCrateFiles(final boolean hasExtension);

    /**
     * Fetches a list of files, from the crates directory.
     *
     * @return list of files
     * @since 1.0.0
     */
    public List<String> getCrateFiles() {
        return getCrateFiles(false);
    }
}