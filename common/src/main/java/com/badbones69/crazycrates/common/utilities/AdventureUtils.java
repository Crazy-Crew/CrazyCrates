package com.badbones69.crazycrates.common.utilities;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import static com.badbones69.crazycrates.common.configuration.files.Locale.PREFIX_COMMAND;
import static com.badbones69.crazycrates.common.configuration.files.Locale.PREFIX_LOGGER;

public class AdventureUtils {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public void send(boolean consoleSender, Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(consoleSender, recipient, true, msg, placeholders);
    }

    public void send(boolean consoleSender, Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        if (msg == null) return;

        for (String part : msg.split("\n")) {
            send(consoleSender, recipient, prefix, parse(part, placeholders));
        }
    }

    public void send(boolean consoleSender, Audience recipient, Component component) {
        send(consoleSender, recipient, true, component);
    }

    public void send(boolean consoleSender, Audience recipient, boolean prefix, Component component) {
        if (consoleSender) {
            recipient.sendMessage(prefix ? parse(PREFIX_LOGGER).append(component) : component);
        } else {
            recipient.sendMessage(prefix ? parse(PREFIX_COMMAND).append(component) : component);
        }
    }

    public Component parse(String msg, TagResolver.Single... placeholders) {
        return miniMessage.deserialize(msg, placeholders);
    }

    public Component parseMessage(String message) {
        return miniMessage.deserialize(message);
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}