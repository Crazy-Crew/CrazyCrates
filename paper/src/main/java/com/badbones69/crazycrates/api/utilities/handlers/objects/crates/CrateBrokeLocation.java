package com.badbones69.crazycrates.api.utilities.handlers.objects.crates;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.Location;

public class CrateBrokeLocation {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    /**
     * The x, y, z coordinates.
     */
    private int x, y, z;

    /**
     * The world name.
     */
    private String world;

    /**
     * The location name.
     */
    private final String locationName;

    /**
     * The crate object.
     */
    private Crate crate;

    /**
     * Builds everything related to broken locations.
     * @param locationName - The name of the location.
     * @param crate - The crate object.
     * @param x - The x coordinate
     * @param y - The y coordinate
     * @param z - The z coordinate
     * @param world - The name of the world.
     */
    public CrateBrokeLocation(String locationName, Crate crate, int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.crate = crate;
        this.locationName = locationName;
    }

    /**
     * @return The broken location.
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @return The x value of the broken location.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the x value to a new value.
     * @param x - The new x value.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return The y value of the broken location.
     */
    public int getY() {
        return y;
    }


    /**
     * Sets the y value to a new value.
     * @param y - The new y value.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return The z value of the broken location.
     */
    public int getZ() {
        return z;
    }

    /**
     * Sets the z value to a new value.
     * @param z - The new z value.
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return The world name.
     */
    public String getWorld() {
        return world;
    }

    /**
     * Sets the world to a new name.
     * @param world - The new world name.
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * @return The crate object.
     */
    public Crate getCrate() {
        return crate;
    }

    /**
     * Set the crate object to a new crate object.
     * @param crate - The new crate object.
     */
    public void setCrate(Crate crate) {
        this.crate = crate;
    }

    /**
     * @return The finalized location where the crate is broken.
     */
    public Location getLocation() {
        return new Location(plugin.getServer().getWorld(world), x, y, z);
    }
}