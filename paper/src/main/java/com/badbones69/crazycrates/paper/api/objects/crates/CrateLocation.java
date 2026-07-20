package com.badbones69.crazycrates.paper.api.objects.crates;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

//todo() move this to the commons module
//todo() add a mapper to create a Location object for Bukkit servers
//todo() add a basic interface that Crate can extend.
public class CrateLocation {
    
    private final String id;
    private final Crate crate;
    private final Location location;
    
    public CrateLocation(@NotNull final String id, @NotNull final Crate crate, @NotNull final Location location) {
        this.location = location;
        this.crate = crate;
        this.id = id;
    }

    public @NotNull final String getLocationAsString() {
        return MiscUtils.location(this.location);
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
        return this.location;
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
     * Get the ID of the location.
     *
     * @return the location's ID.
     */
    public @NotNull final String getID() {
        return this.id;
    }
}