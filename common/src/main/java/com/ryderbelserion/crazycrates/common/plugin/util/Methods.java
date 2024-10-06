package com.ryderbelserion.crazycrates.common.plugin.util;

import com.ryderbelserion.crazycrates.common.plugin.configs.ConfigManager;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.config.ConfigKeys;
import com.ryderbelserion.vital.common.util.AdvUtil;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public class Methods {

    public static String getPrefix() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);
    }

    public static String getPrefix(@NotNull final String msg) {
        if (msg.isEmpty()) return "";

        return getPrefix() + msg;
    }

    public static void sendMessage(@NotNull final Audience audience, final String message, final boolean toggle) {
        if (message.isEmpty()) return;

        if (toggle) {
            final String prefix = getPrefix();

            audience.sendMessage(AdvUtil.parse(message.replace("%prefix%", prefix).replace("%Prefix%", prefix)));

            return;
        }

        audience.sendMessage(AdvUtil.parse(message));
    }
}