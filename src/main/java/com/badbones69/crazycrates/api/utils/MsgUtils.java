package com.badbones69.crazycrates.api.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import static java.util.regex.Matcher.quoteReplacement;

public class MsgUtils {

    public static void sendMessage(CommandSender commandSender, @NotNull final String message, final boolean prefixToggle) {
        if (message.isEmpty()) return;

        String prefix = getPrefix();

        if (commandSender instanceof Player player) {
            if (!prefix.isEmpty() && prefixToggle) player.sendRichMessage(message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix))); else player.sendRichMessage(message);

            return;
        }

        if (!prefix.isEmpty() && prefixToggle) commandSender.sendRichMessage(message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix))); else commandSender.sendRichMessage(message);
    }

    /**
     * @return the {@link String}
     */
    public static @NotNull String getPrefix() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);
    }

    public static @NotNull String getPrefix(@NotNull final String msg) {
        if (msg.isEmpty()) return "";

        return getPrefix() + msg;
    }
}