package com.ryderbelserion.crazycrates.common;

import ch.jalu.configme.SettingsManager;
import com.ryderbelserion.crazycrates.common.plugin.configs.ConfigManager;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.config.ConfigKeys;
import us.crazycrew.crazycrates.platform.ISettings;
import java.util.List;

public class Settings implements ISettings {

    private final SettingsManager config = ConfigManager.getConfig();

    @Override
    public final boolean isPhysicalAcceptsVirtual() {
        return this.config.getProperty(ConfigKeys.physical_accepts_virtual_keys);
    }

    @Override
    public final boolean isPhysicalAcceptsPhysical() {
        return this.config.getProperty(ConfigKeys.physical_accepts_physical_keys);
    }

    @Override
    public final boolean isVirtualAcceptsPhysical() {
        return this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys);
    }

    @Override
    public final List<String> getDisabledWorlds() {
        return this.config.getProperty(ConfigKeys.disabled_worlds);
    }
}