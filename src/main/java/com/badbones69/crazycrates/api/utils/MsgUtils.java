package com.badbones69.crazycrates.api.utils;

import com.ryderbelserion.vital.paper.util.ItemUtil;
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

        final boolean isAdventure = ConfigManager.getConfig().getProperty(ConfigKeys.minimessage_toggle);

        final boolean sendPrefix = !prefix.isEmpty() && prefixToggle;

        if (commandSender instanceof Player player) {
            if (sendPrefix) {
                final String msg = message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix));

                if (isAdventure) {
                    player.sendRichMessage(msg);
                } else {
                    player.sendMessage(ItemUtil.color(msg));
                }
            } else {
                if (isAdventure) {
                    player.sendRichMessage(message);
                } else {
                    player.sendMessage(ItemUtil.color(message));
                }
            }

            return;
        }

        if (sendPrefix) {
            final String msg = message.replaceAll("%prefix%", quoteReplacement(prefix)).replaceAll("%Prefix%", quoteReplacement(prefix));

            if (isAdventure) {
                commandSender.sendRichMessage(msg);
            } else {
                commandSender.sendMessage(ItemUtil.color(msg));
            }
        } else {
            if (isAdventure) {
                commandSender.sendRichMessage(message);
            } else {
                commandSender.sendMessage(ItemUtil.color(message));
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