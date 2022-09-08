package com.badbones69.crazycrates.api.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import com.badbones69.crazycrates.common.utilities.logger.CrazyLogger;
import com.google.inject.Inject;

public class LoggerUtils extends CrazyLogger {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private AdventureUtils adventureUtils;

    public void debug(String message) {
        this.debug(message, plugin.getServer().getConsoleSender(), adventureUtils);
    }
}