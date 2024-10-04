package us.crazycrew.crazycrates;

import net.kyori.adventure.audience.Audience;
import org.checkerframework.checker.nullness.qual.NonNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import java.io.File;
import java.util.List;
import java.util.Map;

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

    /**
     * Parses a string, based on the sender, line, and placeholders
     *
     * @param audience {@link Audience}
     * @param line {@link String}
     * @param placeholders {@link Map} of placeholders
     *
     * @return {@link String}
     * @since 0.9
     */
    String parse(Audience audience, String line, Map<String, String> placeholders);

}