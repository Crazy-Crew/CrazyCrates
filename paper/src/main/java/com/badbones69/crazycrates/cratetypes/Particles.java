package com.badbones69.crazycrates.cratetypes;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.KeyType;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ParticleAnimation;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Particles {
    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();
    public static void openCrate(final Player player, final Location loc, Crate crate, KeyType keyType, HologramController hologramController) {
        if (!crazyManager.takeKeys(1, player, crate, keyType, true)) {
            Methods.failedToTakeKey(player, crate);
            crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        if (hologramController != null) hologramController.removeHologram(loc.getBlock());

        crazyManager.addCrateTask(player, new BukkitRunnable() {
            int tickTillPrize = 0;

            @Override
            public void run() {

                for (ParticleAnimation PA : crate.getParticleAnimations()) {

                    switch (PA.getAnimation().toLowerCase()) {
                        case "spiral_clockwise" ->
                                spiralClockwise(Particle.valueOf(PA.getParticle()), Color.fromRGB(PA.getColor()), loc, tickTillPrize);
                        case "spiral_counterclockwise" ->
                                spiralCounterClockwise(Particle.valueOf(PA.getParticle()), Color.fromRGB(PA.getColor()), loc, tickTillPrize);
                        case "spiral_up" ->
                                spiralUp(Particle.valueOf(PA.getParticle()), Color.fromRGB(PA.getColor()), loc, tickTillPrize);
                        default ->
                                lineUp(Particle.valueOf(PA.getParticle()), Color.fromRGB(PA.getColor()), loc, tickTillPrize);
                    }
                }

                tickTillPrize++;
                if (tickTillPrize == 60) {
                    crazyManager.endCrate(player);
                    QuickCrate.openCrate(player, loc, crate, KeyType.FREE_KEY, hologramController);
                }
            }
        }.runTaskTimer(plugin, 0, 1));
    }

    private static void spawnParticles(Particle particle, Color color, Location location) {
        if (particle == Particle.REDSTONE) {
            location.getWorld().spawnParticle(particle, location, 0, new Particle.DustOptions(color, 1));
        } else {
            location.getWorld().spawnParticle(particle, location, 0);
        }
    }

    private static void spiralUp(Particle particle, Color color, Location loc, int tickTillPrize) {

        double angle = tickTillPrize * 6.0;
        //double r = tickTillPrize / 60.0;

        double x = Math.cos(angle);
        double z = Math.sin(angle);
        double y = tickTillPrize / 15.0;

        Location location = loc.clone().add(.5, 0, .5).add(x, y, z);

        spawnParticles(particle, color, location);

    }
    private static void spiralClockwise(Particle particle, Color color, Location loc, int tickTillPrize) {

        Location particleLocation = loc.clone().add(.5, 3, .5);
        spawnParticles(particle, color, spiralLocations(particleLocation, true).get(tickTillPrize));

    }
    private static void spiralCounterClockwise(Particle particle, Color color, Location loc, int tickTillPrize) {

        Location particleLocation = loc.clone().add(.5, 3, .5);
        spawnParticles(particle, color, spiralLocations(particleLocation, false).get(tickTillPrize));

    }

    private static void lineUp(Particle particle, Color color, Location loc, int tickTillPrize) {

        Location particleLocation = loc.clone().add(.5, 3 - 3/(tickTillPrize * 1.0), .5);
        spawnParticles(particle, color, particleLocation);

    }

    private static ArrayList<Location> spiralLocations(Location center, boolean clockWise) {
        World world = center.getWorld();

        double downWardsDistance = .05;
        double expandingRadius = .08;

        double centerY = center.getY();
        double radius = 0;

        int particleAmount = 10;
        int radiusIncrease = 0;

        int nextLocation = 0;

        double increment = (2*Math.PI) / particleAmount;

        ArrayList<Location> locations = new ArrayList<>();

        for (int i = 0; i < 60; i++) {
            double angle = nextLocation * increment;

            double x;
            double z;

            if (clockWise) {
                x = center.getX() + (radius * Math.cos(angle));
                z = center.getZ() + (radius * Math.sin(angle));
            } else {
                z = center.getZ() - (radius * Math.cos(angle));
                x = center.getX() - (radius * Math.sin(angle));
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

}
