package com.badbones69.crazycrates.api.modules;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;

public abstract class ModuleHandler implements Listener {

    @NotNull
    public final CrazyCrates plugin = CrazyCrates.get();

    public abstract String getModuleName();

    public abstract boolean isEnabled();

    public abstract void reload();

}