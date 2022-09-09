package com.badbones69.crazycrates.common.utilities.logger;

import com.badbones69.crazycrates.common.utilities.AdventureUtils;
import net.kyori.adventure.audience.Audience;

public class CrazyLogger {

    /**
     * @param message - The message.
     * @param recipient - The person to receive the message.
     * @param adventureUtils - The utils class to parse & send it.
     */
    public void debug(String message, Audience recipient, AdventureUtils adventureUtils) {
        adventureUtils.send(true, recipient, parse(" " + message));
    }

    /**
     * @param message - The message we need to parse.
     * @return The message.
     */
    public String parse(String message) {
        return message;
    }
}