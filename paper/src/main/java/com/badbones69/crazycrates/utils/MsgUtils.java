package com.badbones69.crazycrates.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.managers.config.ConfigManager;
import com.badbones69.crazycrates.managers.config.impl.ConfigKeys;
import static java.util.regex.Matcher.quoteReplacement;

public class MsgUtils {

    public static void sendMessage(CommandSender commandSender, @NotNull final String message, final boolean prefixToggle) {
        if (message.isEmpty()) return;

        String prefix = getPrefix();

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

    /**
     * Get the prefix.
     *
     * @return the {@link String}
     */
    public static @NotNull String getPrefix() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);
    }

    /**
     * Get prefix with message.
     *
     * @param msg the message
     * @return the message with prefix
     */
    public static @NotNull String getPrefix(@NotNull final String msg) {
        if (msg.isEmpty()) return "";

        return getPrefix() + msg;
    }
}