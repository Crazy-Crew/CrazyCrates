package com.badbones69.crazycrates.api.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import static java.util.regex.Matcher.quoteReplacement;

public class MsgUtils {

    public static void sendMessage(CommandSender commandSender, String message, boolean prefixToggle) {
        if (message == null || message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendRichMessage(message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendRichMessage(message);

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendRichMessage(message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendRichMessage(message);
    }

    public static String getPrefix() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);
    }

    public static String getPrefix(String msg) {
        return getPrefix() + msg;
    }
}