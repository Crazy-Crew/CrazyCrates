package com.ryderbelserion.crazycrates.common.plugin.bootstrap;

import com.ryderbelserion.crazycrates.common.api.CrazyCratesApiProvider;
import com.ryderbelserion.crazycrates.common.plugin.logger.PluginLogger;
import net.kyori.adventure.audience.Audience;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface CrazyCratesPlugin {

    /**
     * Reloads the plugin
     */
    void reload();

    /**
     * Gets the plugin's storage directory.
     *
     * <p>Paper: ./plugins/CrazyCrates</p>
     *
     * @return the data folder for the platform
     */
    Path getDataDirectory();

    /**
     * Gets the platform's platform logger
     *
     * @return the plugin's logger
     */
    PluginLogger getLogger();

    /**
     * Gets a list of names of online players
     *
     * @return a {@link java.util.List} of usernames
     */
    Collection<String> getPlayerList();

    /**
     * Gets a list of uuids of online players
     *
     * @return a {@link java.util.List} of uuids
     */
    Collection<UUID> getOnlinePlayers();

    /**
     * Gets the user manager
     *
     * @return {@link UserManager}
     */
    UserManager getUserManager();

    /**
     * Gets the api provider for this platform
     *
     * @return the api
     */
    CrazyCratesApiProvider getApiProvider();

    /**
     * Parses a string, based on the sender, line, and placeholders
     *
     * @param audience {@link Audience}
     * @param line {@link String}
     * @param placeholders {@link Map} of placeholders
     *
     * @return {@link String}
     */
    String parse(Audience audience, String line, Map<String, String> placeholders);

    /**
     * Gets a resource file from the jar
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    default InputStream getResourceStream(final String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}