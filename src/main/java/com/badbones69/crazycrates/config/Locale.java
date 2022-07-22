package com.badbones69.crazycrates.config;

import com.badbones69.crazycrates.api.files.AbstractConfig;
import com.badbones69.crazycrates.utils.FileUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.ConsoleCommandSender;

public class Locale extends AbstractConfig {

    private static final Locale LOCALE = new Locale();

    public static void reload() {
        LOCALE.reload(FileUtils.INSTANCE.getLocaleDir().resolve(Config.languageFile), Locale.class);
    }

    public static void send(Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(recipient, true, msg, placeholders);
    }

    public static void send(Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        if (msg == null) return;

        for (String part : msg.split("\n")) {
            send(recipient, prefix, parse(part, placeholders));
        }
    }

    public static void send(Audience recipient, boolean prefix, Component component) {
        if (recipient instanceof ConsoleCommandSender) {
            recipient.sendMessage(prefix ? parse("bleh").append(component) : component);
            return;
        }

        recipient.sendMessage(prefix ? parse("blah").append(component) : component);
    }

    public static Component parse(String msg, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(msg, placeholders);
    }
}