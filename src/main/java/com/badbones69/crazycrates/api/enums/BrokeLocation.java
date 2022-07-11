package com.badbones69.crazycrates.api.enums;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.google.inject.Inject;
import org.bukkit.Location;

public class BrokeLocation {
    
    private int x, y, z;
    private String world;
    private final String locationName;
    private Crate crate;
    
    public BrokeLocation(String locationName, Crate crate, int x, int y, int z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.crate = crate;
        this.locationName = locationName;
    }

    @Inject
    private CrazyCrates plugin;
    
    public String getLocationName() {
        return locationName;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getZ() {
        return z;
    }
    
    public void setZ(int z) {
        this.z = z;
    }
    
    public String getWorld() {
        return world;
    }
    
    public void setWorld(String world) {
        this.world = world;
    }
    
    public Crate getCrate() {
        return crate;
    }
    
    public void setCrate(Crate crate) {
        this.crate = crate;
    }
    
    public Location getLocation() {
        return new Location(plugin.getServer().getWorld(world), x, y, z);
    }
}