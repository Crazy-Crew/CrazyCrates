package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MsgUtils {

    private static @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static @NotNull final FusionPaper fusion = plugin.getFusion();

    public static void message(@NotNull final CommandSender sender, @NotNull final String message, @NotNull final Map<String, String> placeholders, final boolean includePrefix) {
        if (message.isEmpty()) return;

        final String prefix = ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);

        final boolean hasPrefix = !prefix.isEmpty() && includePrefix;

        final Map<String, String> safePlaceholders = new HashMap<>(placeholders);

        if (hasPrefix) {
            safePlaceholders.put("%prefix%", prefix);
            safePlaceholders.put("%Prefix%", prefix);

            safePlaceholders.put("{prefix}", prefix);
        }

        sender.sendMessage(fusion.parse(sender, message, safePlaceholders));
    }

    public static String getRandomNumber(@NotNull final String value) {
        String safeLine = value;

        if (safeLine.contains("%random%")) {
            safeLine = safeLine.replaceAll("%random%", "{random}");
        }

        if (safeLine.contains("{random}")) {
            final String number = safeLine.split(":")[1];

            if (number.contains("-")) {
                final String[] splitter = number.split("-");

                final Optional<Number> minRange = StringUtils.tryParseInt(splitter[0]);
                final Optional<Number> maxRange = StringUtils.tryParseInt(splitter[1]);

                if (minRange.isPresent() && maxRange.isPresent()) {
                    final int minimum = minRange.get().intValue();
                    final int maximum = maxRange.get().intValue();

                    final int amount = MiscUtils.getRandom().nextInt(minimum, maximum);

                    safeLine = safeLine.replace("%s:%s-%s".formatted("{random}", minimum, maximum), String.valueOf(amount));
                } else {
                    fusion.log("warn", "The values supplied with {} seem to not be integers. {}", "{random}", value);
                }
            }
        }

        return safeLine;
    }
}