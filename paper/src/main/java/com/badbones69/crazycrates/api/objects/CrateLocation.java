package com.badbones69.crazycrates.api.objects;

import org.bukkit.Location;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CrateLocation {
    
    private final String id;
    private Crate crate;
    private final Location loc;
    
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
        return this.id;
    }
    
    /**
     * Get the crate that this location is set to.
     * @return The crate that the block is set to.
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * Overwrites the current crate object.
     *
     * @param crate object
     */
    public void setCrate(Crate crate) {
        this.crate = crate;
    }
    
    /**
     * Get the crate type of the crate.
     * @return The type of crate the crate is.
     */
    public CrateType getCrateType() {
        return this.crate.getCrateType();
    }
    
    /**
     * Get the physical location of the crate.
     * @return The location of the crate.
     */
    public Location getLocation() {
        return this.loc;
    }
}