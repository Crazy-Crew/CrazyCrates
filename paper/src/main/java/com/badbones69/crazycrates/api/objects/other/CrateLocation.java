package com.badbones69.crazycrates.api.objects.other;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CrateLocation {
    
    private final String id;
    private final Crate crate;
    private final Location loc;
    
    public CrateLocation(@NotNull final String id, @NotNull final Crate crate, @NotNull final Location loc) {
        this.id = id;
        this.crate = crate;
        this.loc = loc;
    }
    
    /**
     * Get the ID of the location.
     *
     * @return the location's ID.
     */
    public @NotNull final String getID() {
        return this.id;
    }
    
    /**
     * Get the crate that this location is set to.
     *
     * @return the crate that the block is set to.
     */
    public @NotNull final Crate getCrate() {
        return this.crate;
    }
    
    /**
     * Get the crate type of the crate.
     *
     * @return the type of crate the crate is.
     */
    public @NotNull final CrateType getCrateType() {
        return this.crate.getCrateType();
    }
    
    /**
     * Get the physical location of the crate.
     *
     * @return the location of the crate.
     */
    public @NotNull final Location getLocation() {
        return this.loc;
    }
}