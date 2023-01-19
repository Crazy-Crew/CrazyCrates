package com.badbones69.crazycrates.api.enums;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Location;

public class BrokeLocation {

    /**
     * Empty values that get instantiated below.
     */
    private int x, y, z;
    private String world;
    private final String locationName;
    private Crate crate;

    /**
     * Fetch the plugin instance.
     */
    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    /**
     * Builds a location that represents a broken crate.
     * Usually never activates.......
     *
     * @param locationName The location name
     * @param crate The crate object
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @param world The world name
     */
    public BrokeLocation(String locationName, Crate crate, int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.crate = crate;
        this.locationName = locationName;
    }

    /**
     * @return The location name
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * @return The X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Set the X coordinate.
     *
     * @param x The new X coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return The Y coordinate
     */
    public int getY() {
        return y;
    }


    /**
     * Set the Y coordinate.
     *
     * @param y The new Y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return The Z coordinate
     */
    public int getZ() {
        return z;
    }

    /**
     * Set the Z coordinate.
     *
     * @param z The new Z coordinate
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return The world name
     */
    public String getWorld() {
        return world;
    }

    /**
     * Set the World name.
     *
     * @param world The world name
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * @return The crate object
     */
    public Crate getCrate() {
        return crate;
    }

    /**
     * Set the crate object.
     *
     * @param crate The new crate object
     */
    public void setCrate(Crate crate) {
        this.crate = crate;
    }

    /**
     * Get the location of the Broken Crate.
     *
     * @return The location of the broken crate
     */
    public Location getLocation() {
        return new Location(plugin.getServer().getWorld(world), x, y, z);
    }
}