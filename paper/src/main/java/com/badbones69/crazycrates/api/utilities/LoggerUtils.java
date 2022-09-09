package com.badbones69.crazycrates.api.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import com.badbones69.crazycrates.common.utilities.logger.CrazyLogger;

public class LoggerUtils extends CrazyLogger {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final AdventureUtils adventureUtils;

    public LoggerUtils(AdventureUtils adventureUtils) {
        this.adventureUtils = adventureUtils;
    }

    public void debug(String message) {
        this.debug(message, plugin.getServer().getConsoleSender(), adventureUtils);
    }
}