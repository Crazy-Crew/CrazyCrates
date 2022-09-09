package com.badbones69.crazycrates.api.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import com.badbones69.crazycrates.common.utilities.logger.CrazyLogger;

public class LoggerUtils extends CrazyLogger {

    // Global Methods.
    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final AdventureUtils adventureUtils = plugin.getStarter().getAdventureUtils();

    // Class Internals.

    /**
     * Sends a message to console.
     * @param message - The message to be sent.
     */
    public void debug(String message) {
        this.debug(message, plugin.getServer().getConsoleSender(), adventureUtils);
    }
}