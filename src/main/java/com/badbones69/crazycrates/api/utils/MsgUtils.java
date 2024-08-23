package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.CrazyCrates;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import static java.util.regex.Matcher.quoteReplacement;

public class MsgUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    public static void sendMessage(CommandSender commandSender, @NotNull final String message, final boolean prefixToggle) {
        if (message.isEmpty()) return;

        String prefix = getPrefix();

        final boolean sendPrefix = !prefix.isEmpty() && prefixToggle;

        if (commandSender instanceof Player player) {
            if (sendPrefix) {
                final String msg = message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix));

                if (plugin.isLegacy()) {
                    player.sendMessage(ItemUtil.color(msg));
                } else {
                    player.sendRichMessage(msg);
                }
            } else {
                if (plugin.isLegacy()) {
                    player.sendMessage(ItemUtil.color(message));
                } else {
                    player.sendRichMessage(message);
                }
            }

            return;
        }

        if (sendPrefix) {
            final String msg = message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix));

            if (plugin.isLegacy()) {
                commandSender.sendMessage(ItemUtil.color(msg));
            } else {
                commandSender.sendRichMessage(msg);
            }
        } else {
            if (plugin.isLegacy()) {
                commandSender.sendMessage(ItemUtil.color(message));
            } else {
                commandSender.sendRichMessage(message);
            }
        }
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