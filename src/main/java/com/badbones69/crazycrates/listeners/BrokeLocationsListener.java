package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.BrokeLocation;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.support.holograms.HologramManager;
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

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (this.crateManager.getBrokeLocations().isEmpty()) return;

        int fixedAmount = 0;

        final List<BrokeLocation> fixedWorlds = new ArrayList<>();

        for (final BrokeLocation brokeLocation : this.crateManager.getBrokeLocations()) {
            final Location location = brokeLocation.getLocation();

            if (location.getWorld() != null) {
                if (brokeLocation.getCrate() != null) {
                    CrateLocation crateLocation = new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location);

                    this.crateManager.addLocation(crateLocation);

                    final HologramManager manager = this.crateManager.getHolograms();

                    final Crate crate = crateLocation.getCrate();

                    if (manager != null && crate.getHologram().isEnabled()) {
                        manager.createHologram(location, crate, crateLocation.getID());
                    }

                    fixedWorlds.add(brokeLocation);
                    fixedAmount++;
                }
            }
        }

        this.crateManager.removeBrokeLocation(fixedWorlds);

        if (MiscUtils.isLogging()) {
            this.plugin.getComponentLogger().warn("Fixed {} broken crate locations.", fixedAmount);

            if (this.crateManager.getBrokeLocations().isEmpty()) this.plugin.getComponentLogger().warn("All broken crate locations have been fixed.");
        }
    }
}