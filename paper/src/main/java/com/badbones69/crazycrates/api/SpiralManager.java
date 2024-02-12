package com.badbones69.crazycrates.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SpiralManager {

    @NotNull
    private static List<Location> getLocations(Location center, boolean clockWise) {
        World world = center.getWorld();

        double downWardsDistance = .05;
        double expandingRadius = .08;

        double centerY = center.getY();
        double radius = 0;

        int particleAmount = 10;
        int radiusIncrease = 0;

        int nextLocation = 0;

        double increment = (2*Math.PI) / particleAmount;

        List<Location> locations = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            double angle = nextLocation * increment;

            double x;
            double z;

            if (clockWise) {
                x = center.getX() + (radius * Math.cos(angle));
                z = center.getZ() + (radius * Math.sin(angle));
            } else {
                x = center.getX() - (radius * Math.cos(angle));
                z = center.getZ() - (radius * Math.sin(angle));
            }

            locations.add(new Location(world, x, centerY, z));
            centerY -= downWardsDistance;
            nextLocation++;
            radiusIncrease++;

            if (radiusIncrease == 6) {
                radiusIncrease = 0;
                radius += expandingRadius;
            }

            if (nextLocation == 10) nextLocation = 0;
        }

        return locations;
    }

    @NotNull
    public static List<Location> getSpiralLocationClockwise(Location center) {
        return getLocations(center, true);
    }

    @NotNull
    public static List<Location> getSpiralLocationCounterClockwise(Location center) {
        return getLocations(center, false);
    }
}