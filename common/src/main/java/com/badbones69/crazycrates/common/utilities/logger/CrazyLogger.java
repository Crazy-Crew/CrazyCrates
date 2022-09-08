package com.badbones69.crazycrates.common.utilities.logger;

import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import net.kyori.adventure.audience.Audience;

public class CrazyLogger {

    public void debug(String message, Audience recipient, AdventureUtils adventureUtils) {
        adventureUtils.send(true, recipient, parse(" " + message));
    }

    public String parse(String message) {
        return message;
    }
}