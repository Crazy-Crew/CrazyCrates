package com.badbones69.crazycrates.common.utilities;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import static com.badbones69.crazycrates.common.configuration.files.Locale.PREFIX_COMMAND;
import static com.badbones69.crazycrates.common.configuration.files.Locale.PREFIX_LOGGER;

public class AdventureUtils {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    /**
     * @param consoleSender - If the recipient is console or not.
     * @param recipient - The recipient.
     * @param msg - The message content.
     * @param placeholders - Any placeholders.
     */
    public void send(boolean consoleSender, Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(consoleSender, recipient, true, msg, placeholders);
    }

    /**
     * @param consoleSender - If the recipient is console or not.
     * @param recipient - The recipient.
     * @param prefix - If we should include the prefix or not.
     * @param msg - The message content.
     * @param placeholders - Any placeholders.
     */
    public void send(boolean consoleSender, Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        if (msg == null) return;

        for (String part : msg.split("\n")) {
            send(consoleSender, recipient, prefix, parse(part, placeholders));
        }
    }

    /**
     * @param consoleSender - If the recipient is console or not.
     * @param recipient - The recipient.
     * @param component - The message component.
     */
    public void send(boolean consoleSender, Audience recipient, Component component) {
        send(consoleSender, recipient, true, component);
    }

    /**
     * @param consoleSender - If the recipient is console or not.
     * @param recipient - The recipient.
     * @param prefix - If we should include the prefix or not.
     * @param component - The message component.
     */
    public void send(boolean consoleSender, Audience recipient, boolean prefix, Component component) {
        if (consoleSender) {
            recipient.sendMessage(prefix ? parse(PREFIX_LOGGER).append(component) : component);
        } else {
            recipient.sendMessage(prefix ? parse(PREFIX_COMMAND).append(component) : component);
        }
    }

    /**
     * @param msg - The message content.
     * @param placeholders - Any placeholders.
     * @return The converted message with placeholders.
     */
    public Component parse(String msg, TagResolver.Single... placeholders) {
        return miniMessage.deserialize(msg, placeholders);
    }

    /**
     * @param message - The message we want to convert.
     * @return The converted message.
     */
    public Component parseMessage(String message) {
        return miniMessage.deserialize(message);
    }

    /**
     * @return The minimessage instance.
     */
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}