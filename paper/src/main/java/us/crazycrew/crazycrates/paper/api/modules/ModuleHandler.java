package us.crazycrew.crazycrates.paper.api.modules;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;

public abstract class ModuleHandler implements Listener {

    @NotNull
    public final CrazyCrates plugin = CrazyCrates.get();

    public abstract String getModuleName();

    public abstract boolean isEnabled();

    public abstract void reload();

}