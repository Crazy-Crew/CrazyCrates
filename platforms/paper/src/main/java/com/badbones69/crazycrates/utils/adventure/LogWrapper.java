package com.badbones69.crazycrates.utils.adventure;

import com.badbones69.crazycrates.configs.Config;
import net.dehya.ruby.utils.AdventureUtils;
import net.dehya.ruby.utils.LoggerUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class LogWrapper {

    /**
     * Send a message or log a message.
     *
     * @param consoleSender - If the recipient is console or not.
     * @param recipient - The recipient.
     * @param prefix - If we should include the prefix or not.
     * @param component - The message component.
     */
    public static void send(boolean consoleSender, Audience recipient, boolean prefix, String component) {
        if (consoleSender) {
            LoggerUtils.info(Config.PREFIX_LOGGER + component);
        } else {
            recipient.sendMessage(prefix ? AdventureUtils.parse(Config.PREFIX_COMMAND).append(Component.text(component)) : Component.text(component));
        }
    }
}