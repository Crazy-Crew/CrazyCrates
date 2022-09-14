package com.badbones69.crazycrates.common.utilities.logger;

import com.badbones69.crazycrates.common.CommonsBuilder;
import com.badbones69.crazycrates.common.CommonsStarter;
import net.kyori.adventure.audience.Audience;

public class CrazyLogger {

    private final CommonsBuilder commonsStarter = CommonsStarter.getCommonsBuilder();

    /**
     * @param message - The message.
     * @param recipient - The person to receive the message.
     */
    public void debug(String message, Audience recipient) {
        commonsStarter.getAdventureUtils().send(true, recipient, parse(" " + message));
    }

    /**
     * @param message - The message we need to parse.
     * @return The message.
     */
    public String parse(String message) {
        return message;
    }
}