package com.badbones69.crazycrates.api;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.platform.config.ConfigManager;
import com.badbones69.crazycrates.platform.config.impl.ConfigKeys;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.platform.ISettings;
import java.util.Collections;
import java.util.List;

public class Settings implements ISettings {

    private final @NotNull SettingsManager config = ConfigManager.getConfig();

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
        return Collections.unmodifiableList(this.config.getProperty(ConfigKeys.disabled_worlds));
    }
}