package com.badbones69.crazycrates.api.utilities.handlers.objects.crates;

import com.badbones69.crazycrates.common.enums.crates.CrateType;
import org.bukkit.Location;

public class CrateLocation {

    /**
     * The id of the crate.
     */
    private final String id;

    /**
     * The crate object.
     */
    private final Crate crate;

    /**
     * The location of the crate.
     */
    private final Location loc;

    /**
     * Everything regarding the Crate Location.
     * @param id - The location id
     * @param crate - The crate object
     * @param loc - The location of the crate.
     */
    public CrateLocation(String id, Crate crate, Location loc) {
        this.id = id;
        this.crate = crate;
        this.loc = loc;
    }
    
    /**
     * Get the ID of the location.
     * @return The location's ID.
     */
    public String getID() {
        return id;
    }
    
    /**
     * Get the crate that this location is set to.
     * @return The crate that the block is set to.
     */
    public Crate getCrate() {
        return crate;
    }
    
    /**
     * Get the crate type of the crate.
     * @return The type of crate the crate is.
     */
    public CrateType getCrateType() {
        return crate.getCrateType();
    }
    
    /**
     * Get the physical location of the crate.
     * @return The location of the crate.
     */
    public Location getLocation() {
        return loc;
    }
}