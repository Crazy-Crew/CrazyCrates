package com.ryderbelserion.crazycrates.common.plugin.util;

import com.ryderbelserion.crazycrates.common.plugin.configs.ConfigManager;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.config.ConfigKeys;
import org.jetbrains.annotations.NotNull;

public class Methods {

    public static String getPrefix() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.command_prefix);
    }

    public static String getPrefix(@NotNull final String msg) {
        if (msg.isEmpty()) return "";

        return getPrefix() + msg;
    }
}