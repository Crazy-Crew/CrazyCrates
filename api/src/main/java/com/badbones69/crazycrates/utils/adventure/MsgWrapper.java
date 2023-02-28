package com.badbones69.crazycrates.utils.adventure;

import com.badbones69.crazycrates.configs.Config;
import net.dehya.ruby.registry.RubyLogger;
import net.dehya.ruby.utils.AdventureUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class MsgWrapper {

    /**
     * Send a basic message to a player.
     *
     * @param recipient the recipient.
     * @param prefix if we should include the prefix or not.
     * @param component the message component.
     */
    public static void send(Audience recipient, boolean prefix, String component) {
        recipient.sendMessage(prefix ? AdventureUtils.parse(Config.PREFIX_COMMAND).append(Component.text(component)) : Component.text(component));
    }

    /**
     * Send a log message to console if VERBOSE_LOGGING is enabled.
     *
     * @param component the message.
     */
    public static void send(String component) {
        if (Config.VERBOSE_LOGGING) RubyLogger.info(component);
    }
}