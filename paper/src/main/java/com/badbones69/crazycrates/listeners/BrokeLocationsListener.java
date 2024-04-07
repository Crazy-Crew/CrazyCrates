package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.other.BrokeLocation;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (this.crateManager.getBrokeLocations().isEmpty()) return;

        int fixedAmount = 0;
        List<BrokeLocation> fixedWorlds = new ArrayList<>();

        for (BrokeLocation brokeLocation : this.crateManager.getBrokeLocations()) {
            Location location = brokeLocation.getLocation();

            if (location.getWorld() != null) {
                if (brokeLocation.getCrate() != null) {
                    this.crateManager.addLocation(new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location));

                    if (brokeLocation.getCrate().getHologram().isEnabled() && this.crateManager.getHolograms() != null) this.crateManager.getHolograms().createHologram(location, brokeLocation.getCrate());

                    fixedWorlds.add(brokeLocation);
                    fixedAmount++;
                }
            }
        }

        this.crateManager.removeBrokeLocation(fixedWorlds);

        if (MiscUtils.isLogging()) {
            this.plugin.getLogger().warning("Fixed " + fixedAmount + " broken crate locations.");

            if (this.crateManager.getBrokeLocations().isEmpty()) this.plugin.getLogger().warning("All broken crate locations have been fixed.");
        }
    }
}