package com.badbones69.crazycrates.paper.cache.objects;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.cache.enums.ActiveStatus;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import org.bukkit.Location;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class ActiveCrate {

    private final ActiveStatus activeStatus;
    private final Location location;
    private final Crate crate;
    private final int amount;

    public ActiveCrate(final ActiveStatus activeStatus, final Location location, final int amount, final Crate crate) {
        this.activeStatus = activeStatus;
        this.location = location;
        this.amount = amount;
        this.crate = crate;
    }

    public ActiveStatus getActiveStatus() {
        return this.activeStatus;
    }

    public String getLocationAsString() {
        return MiscUtils.location(this.location);
    }

    public Location getLocation() {
        return this.location;
    }

    public Crate getCrate() {
        return this.crate;
    }

    public int getAmount() {
        return this.amount;
    }
}