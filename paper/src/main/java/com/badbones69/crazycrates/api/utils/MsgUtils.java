package com.badbones69.crazycrates.api.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Matcher.quoteReplacement;

@SuppressWarnings("ALL")
public class MsgUtils {

    public static String color(String message) {
        Matcher matcher = Pattern.compile("#[a-fA-F\\d]{6}").matcher(message);
        StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static void sendMessage(CommandSender commandSender, String message, boolean prefixToggle) {
        if (message == null || message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendMessage(color(message));

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendMessage(color(message.replaceAll("%prefix%", quoteReplacement(prefix))).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendMessage(color(message));
    }

    public static String getPrefix() {
        return color(ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix));
    }

    public static String getPrefix(String msg) {
        return color(getPrefix() + msg);
    }

    public static String sanitizeColor(String msg) {
        return sanitizeFormat(color(msg));
    }

    public static String sanitizeFormat(String string) {
        return TextComponent.toLegacyText(TextComponent.fromLegacyText(string));
    }

    public static final Pattern STRIP_PATTERN = Pattern.compile("(?:(?<!<@)&|[§\u007F])(?i)[0-9a-fklmnorx]");

    public static String stripColor(String msg) {
        if (msg.isEmpty()) return "";

        return STRIP_PATTERN.matcher(msg).replaceAll("");
    }

    public static String removeColor(String msg) {
        return ChatColor.stripColor(msg);
    }
}