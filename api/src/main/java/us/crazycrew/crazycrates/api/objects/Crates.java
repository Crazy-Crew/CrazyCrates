package us.crazycrew.crazycrates.api.objects;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.List;

public abstract class Crates {

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