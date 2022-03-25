package com.badbones69.crazycrates.v2.crates.sessions;

import com.badbones69.crazycrates.v2.utils.quadcrates.StructuresHandler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class QuadCrateSession {

    private final Location spawnLocation;
    private final Player player;

    public QuadCrateSession(Player player, Location spawnLocation) {
        this.player = player;
        this.spawnLocation = spawnLocation;
    }

    public void startCrate(String structureName) {
        Location newLocation = new Location(spawnLocation.getWorld(), spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ(), 0.5F, 0.5F);

        new StructuresHandler(structureName, player.getLocation()).pasteStructure();
        player.teleport(newLocation);
    }
}