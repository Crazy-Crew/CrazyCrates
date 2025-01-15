package com.badbones69.crazycrates.paper.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpiralManager {

    private static @NotNull List<Location> getLocations(@NotNull final Location center, final boolean clockWise) {
        final World world = center.getWorld();

        double downWardsDistance = .05;
        double expandingRadius = .08;

        double centerY = center.getY();
        double radius = 0;

        int particleAmount = 10;
        int radiusIncrease = 0;

        int nextLocation = 0;

        final double increment = (2*Math.PI) / particleAmount;

        final List<Location> locations = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            final double angle = nextLocation * increment;

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

        return Collections.unmodifiableList(locations);
    }

    public static @NotNull List<Location> getSpiralLocationClockwise(@NotNull final Location center) {
        return getLocations(center, true);
    }

    public static @NotNull List<Location> getSpiralLocationCounterClockwise(@NotNull final Location center) {
        return getLocations(center, false);
    }
}