package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.BrokenLocations;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.OldFileManager;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.config.Config;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    private final CrazyManager crazyManager = CrazyManager.getInstance();
    private final OldFileManager oldFileManager = OldFileManager.getInstance();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!crazyManager.getBrokeCrateLocations().isEmpty()) {
            int fixedAmount = 0;
            List<BrokenLocations> fixedWorlds = new ArrayList<>();

            for (BrokenLocations brokenLocations : crazyManager.getBrokeCrateLocations()) {
                Location location = brokenLocations.getLocation();

                if (location.getWorld() != null) {
                    crazyManager.getCrateLocations().add(new CrateLocation(brokenLocations.getLocationName(), brokenLocations.getCrate(), location));

                    if (crazyManager.getHologramController() != null) {
                        crazyManager.getHologramController().createHologram(location.getBlock(), brokenLocations.getCrate());
                    }

                    fixedWorlds.add(brokenLocations);
                    fixedAmount++;
                }
            }

            crazyManager.getBrokeCrateLocations().removeAll(fixedWorlds);

            if (Config.verbose) {
                crazyManager.getPlugin().getLogger().warning("Fixed " + fixedAmount + " broken crate locations.");

                if (crazyManager.getBrokeCrateLocations().isEmpty()) crazyManager.getPlugin().getLogger().warning("All broken crate locations have been fixed.");
            }
        }
    }
}