package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.enums.BrokeLocation;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    private static final FileManager fileManager = plugin.getStarter().getFileManager();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!crazyManager.getBrokeCrateLocations().isEmpty()) {
            int fixedAmount = 0;
            List<BrokeLocation> fixedWorlds = new ArrayList<>();

            for (BrokeLocation brokeLocation : crazyManager.getBrokeCrateLocations()) {
                Location location = brokeLocation.getLocation();

                if (location.getWorld() != null) {
                    if (brokeLocation.getCrate() != null) {
                        crazyManager.getCrateLocations().add(new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location));

                        if (brokeLocation.getCrate().getHologram().isEnabled() && crazyManager.getHologramController() != null) crazyManager.getHologramController().createHologram(location.getBlock(), brokeLocation.getCrate());

                        fixedWorlds.add(brokeLocation);
                        fixedAmount++;
                    }
                }
            }

            crazyManager.getBrokeCrateLocations().removeAll(fixedWorlds);

            if (fileManager.isLogging()) {
                plugin.getLogger().warning("Fixed " + fixedAmount + " broken crate locations.");

                if (crazyManager.getBrokeCrateLocations().isEmpty()) plugin.getLogger().warning("All broken crate locations have been fixed.");
            }
        }
    }
}