package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import java.util.UUID;

public abstract class HologramManager {
    
    public abstract void createHologram(Location location, Crate crate);

    public abstract void removeHologram(Location location);

    public abstract void removeAllHolograms();

    public abstract boolean isEmpty();

    protected String name() {
        return "crazycrates-" + UUID.randomUUID();
    }

    protected Vector getVector(Crate crate) {
        return new Vector(0.5, crate.getHologram().getHeight(), 0.5);
    }
}