package us.crazycrew.crazycrates;

import org.checkerframework.checker.nullness.qual.NonNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import java.io.File;
import java.util.List;

/**
 * CrazyCrates' API
 *
 * @author ryderbelserion
 * @version 0.9
 * @since 0.9
 */
public interface CrazyCratesApi {

    /**
     * Reloads the plugin
     *
     * @since 0.9
     */
    void reload();

    /**
     * Gets the crates folder
     *
     * @return {@link File}
     * @since 0.9
     */
    @NonNull File getCrateFolder();

    /**
     * Gets the plugin/mods folder
     *
     * @return {@link File}
     * @since 0.9
     */
    @NonNull File getDataFolder();

    /**
     * Gets a list of crate files
     *
     * @return {@link List<String>}
     * @since 0.9
     */
    List<String> getCrateFiles();

    /**
     * Gets the instance of the user manager
     *
     * @return {@link UserManager}
     * @since 0.9
     */
    @NonNull UserManager getUserManager();

    /**
     * Gets a current amount of settings
     *
     * @return {@link ISettings}
     * @since 0.9
     */
    @NonNull ISettings getSettings();

}