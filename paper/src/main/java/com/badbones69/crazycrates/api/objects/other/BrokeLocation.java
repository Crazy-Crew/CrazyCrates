package com.badbones69.crazycrates.api.objects.other;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BrokeLocation {

    /**
     * Empty values that get instantiated below.
     */
    private int x, y, z;
    private String world;
    private final String locationName;
    private Crate crate;

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    /**
     * Builds a location that represents a broken crate.
     * Usually never activates.......
     *
     * @param locationName the location name
     * @param crate the crate object
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     * @param world the world name
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
     * @return the location name
     */
    public String getLocationName() {
        return this.locationName;
    }

    /**
     * @return the X coordinate
     */
    public int getX() {
        return this.x;
    }

    /**
     * Set the X coordinate.
     *
     * @param x the new X coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the Y coordinate
     */
    public int getY() {
        return this.y;
    }


    /**
     * Set the Y coordinate.
     *
     * @param y the new Y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the Z coordinate
     */
    public int getZ() {
        return this.z;
    }

    /**
     * Set the Z coordinate.
     *
     * @param z the new Z coordinate
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return the world name
     */
    public String getWorld() {
        return this.world;
    }

    /**
     * Set the World name.
     *
     * @param world the world name
     */
    public void setWorld(String world) {
        this.world = world;
    }

    /**
     * @return the crate object
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * Set the crate object.
     *
     * @param crate the new crate object
     */
    public void setCrate(Crate crate) {
        this.crate = crate;
    }

    /**
     * Get the location of the Broken Crate.
     *
     * @return the location of the broken crate
     */
    public Location getLocation() {
        return new Location(this.plugin.getServer().getWorld(this.world), this.x, this.y, this.z);
    }
}