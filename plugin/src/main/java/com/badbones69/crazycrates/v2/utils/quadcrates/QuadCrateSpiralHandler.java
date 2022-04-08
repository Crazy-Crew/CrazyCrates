package com.badbones69.crazycrates.v2.utils.quadcrates;

import com.badbones69.crazycrates.v2.utils.interfaces.SpiralControl;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class QuadCrateSpiralHandler implements SpiralControl {

    @Override
    public @NotNull ArrayList<Location> getSpiralLocationClockwise(Location center) {
        World world = center.getWorld();
        double downwardsDistance = .05;
        double expandingRadius = .08;
        double y = center.getY();
        double radius = 0;
        int amount = 10;//Amount of particles in each circle
        int increaseRadius = 0;
        int nextLocation = 0;//The limit of how far the circle goes before reset.
        double increment = (2 * Math.PI) / amount;//Spacing
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            double angle = nextLocation * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, y, z));
            y -= downwardsDistance;
            nextLocation++;
            increaseRadius++;
            if (increaseRadius == 6) {
                increaseRadius = 0;
                radius += expandingRadius;
            }
            if (nextLocation == 10) {
                nextLocation = 0;
            }
        }
        return locations;
    }

    @Override
    public @NotNull ArrayList<Location> getSpiralLocationCounterClockwise(Location center) {
        World world = center.getWorld();
        double downwardsDistance = .05;
        double expandingRadius = .08;
        double y = center.getY();
        double radius = 0;
        int amount = 10;//Amount of particles in each circle
        int increaseRadius = 0;
        int nextLocation = 0;//The limit of how far the circle goes before reset.
        double increment = (2 * Math.PI) / amount;//Spacing
        ArrayList<Location> locations = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            double angle = nextLocation * increment;
            double x = center.getX() - (radius * Math.cos(angle));
            double z = center.getZ() - (radius * Math.sin(angle));
            locations.add(new Location(world, x, y, z));
            y -= downwardsDistance;
            nextLocation++;
            increaseRadius++;
            if (increaseRadius == 6) {
                increaseRadius = 0;
                radius += expandingRadius;
            }
            if (nextLocation == 10) {
                nextLocation = 0;
            }
        }
        return locations;
    }
}