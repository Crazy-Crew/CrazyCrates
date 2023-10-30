package com.badbones69.crazycrates.paper.listeners;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.enums.BrokeLocation;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;

import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (this.crateManager.getBrokeLocations().isEmpty()) return;

        int fixedAmount = 0;
        ArrayList<BrokeLocation> fixedWorlds = new ArrayList<>();

        for (BrokeLocation brokeLocation : this.crateManager.getBrokeLocations()) {
            Location location = brokeLocation.getLocation();

            if (location.getWorld() != null) {
                if (brokeLocation.getCrate() != null) {
                    this.crateManager.addLocation(new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location));

                    if (brokeLocation.getCrate().getHologram().isEnabled() && this.crateManager.getHolograms() != null)
                        this.crateManager.getHolograms().createHologram(location.getBlock(), brokeLocation.getCrate());

                    fixedWorlds.add(brokeLocation);
                    fixedAmount++;
                }
            }
        }

        this.crateManager.removeBrokeLocation(fixedWorlds);

        if (this.plugin.isLogging()) {
            this.plugin.getLogger().warning("Fixed " + fixedAmount + " broken crate locations.");

            if (this.crateManager.getBrokeLocations().isEmpty()) this.plugin.getLogger().warning("All broken crate locations have been fixed.");
        }
    }
}