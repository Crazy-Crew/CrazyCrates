package com.badbones69.crazycrates.api.objects.other;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrokeLocation {

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    /**
     * Empty values that get instantiated below.
     */
    private final int x, y, z;
    private final String world;
    private final String locationName;
    private final Crate crate;

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
    public BrokeLocation(@NotNull final String locationName, @Nullable final Crate crate, final int x, final int y, final int z, @NotNull final String world) {
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
    public final String getLocationName() {
        return this.locationName;
    }

    /**
     * @return the X coordinate
     */
    public final int getX() {
        return this.x;
    }

    /**
     * @return the Y coordinate
     */
    public final int getY() {
        return this.y;
    }

    /**
     * @return the Z coordinate
     */
    public final int getZ() {
        return this.z;
    }

    /**
     * @return the world name
     */
    public @NotNull final String getWorld() {
        return this.world;
    }

    /**
     * @return the crate plugin
     */
    public Crate getCrate() {
        return this.crate;
    }

    /**
     * Get the location of the Broken Crate.
     *
     * @return the location of the broken crate
     */
    public @NotNull final Location getLocation() {
        return new Location(this.plugin.getServer().getWorld(this.world), this.x, this.y, this.z);
    }
}