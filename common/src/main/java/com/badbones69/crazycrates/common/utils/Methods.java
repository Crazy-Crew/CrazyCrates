package com.badbones69.crazycrates.common.utils;

import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
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