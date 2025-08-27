package com.badbones69.crazycrates.paper.support.holograms;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.UUID;

public abstract class HologramManager {

    protected CrazyCrates plugin = CrazyCrates.getPlugin();

    protected String name = this.plugin.getName().toLowerCase();
    
    public abstract void createHologram(@NotNull final Location location, @NotNull final Crate crate, @NotNull final String id);

    public abstract void removeHologram(@NotNull final String id);

    public abstract boolean exists(@NotNull final String id);

    public abstract void purge(final boolean isShutdown);

    public abstract String getName();

    protected @NotNull final String name() {
        return this.name + "-" + UUID.randomUUID();
    }

    protected @NotNull final String name(final String id) {
        return this.name + "-" + id;
    }

    protected @NotNull final Vector getVector(@NotNull final Crate crate) {
        return new Vector(0.5, crate.getHologram().getHeight(), 0.5);
    }
}