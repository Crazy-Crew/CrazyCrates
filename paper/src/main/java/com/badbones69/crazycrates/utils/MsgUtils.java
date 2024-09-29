package com.badbones69.crazycrates.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import static com.badbones69.crazycrates.common.utils.Methods.getPrefix;
import static java.util.regex.Matcher.quoteReplacement;

public class MsgUtils {

    public static void sendMessage(CommandSender commandSender, @NotNull final String message, final boolean prefixToggle) {
        if (message.isEmpty()) return;

        final String prefix = getPrefix();

        final boolean sendPrefix = !prefix.isEmpty() && prefixToggle;

        if (commandSender instanceof Player player) {
            if (sendPrefix) {
                final String msg = message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix));

                player.sendRichMessage(msg);
            } else {
                player.sendRichMessage(message);
            }

            return;
        }

        if (sendPrefix) {
            final String msg = message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix));

            commandSender.sendRichMessage(msg);
        } else {
            commandSender.sendRichMessage(message);
        }
    }
}