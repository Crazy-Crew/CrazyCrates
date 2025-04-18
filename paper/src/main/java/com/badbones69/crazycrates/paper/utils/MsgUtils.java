package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import static java.util.regex.Matcher.quoteReplacement;

public class MsgUtils {

    public static void sendMessage(@NotNull final CommandSender sender, @NotNull final String message, final boolean prefixToggle) {
        if (message.isEmpty()) return;

        final String prefix = ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);

        final boolean sendPrefix = !prefix.isEmpty() && prefixToggle;

        final String msg = sendPrefix ? message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix)) : message;

        sender.sendRichMessage(msg);
    }
}