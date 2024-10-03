package com.ryderbelserion.crazycrates.common.plugin.bootstrap;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.UUID;

public interface CrazyCratesBootstrap {

    /**
     * Gets the plugin's storage directory.
     *
     * <p>Paper: ./plugins/CrazyCrates</p>
     *
     * @return the data folder for the platform
     */
    Path getDataDirectory();

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
     * Gets a resource file from the jar
     *
     * @param path the path of the file
     * @return the file as an input stream
     */
    default InputStream getResourceStream(final String path) {
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}