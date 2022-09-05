package com.badbones69.crazycrates.utilities;

import com.google.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.ConsoleCommandSender;
import static com.badbones69.crazycrates.modules.config.files.Locale.PREFIX_COMMAND;
import static com.badbones69.crazycrates.modules.config.files.Locale.PREFIX_LOGGER;

@Singleton
public class AdventureUtils {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public void send(Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(recipient, true, msg, placeholders);
    }

    public void send(Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        if (msg == null) return;

        for (String part : msg.split("\n")) {
            send(recipient, prefix, parse(part, placeholders));
        }
    }

    public void send(Audience recipient, Component component) {
        send(recipient, true, component);
    }

    public void send(Audience recipient, boolean prefix, Component component) {
        if (recipient instanceof ConsoleCommandSender) {
            recipient.sendMessage(prefix ? parse(PREFIX_LOGGER).append(component) : component);
        } else {
            recipient.sendMessage(prefix ? parse(PREFIX_COMMAND).append(component) : component);
        }
    }

    public Component parse(String msg, TagResolver.Single... placeholders) {
        return miniMessage.deserialize(msg, placeholders);
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
}