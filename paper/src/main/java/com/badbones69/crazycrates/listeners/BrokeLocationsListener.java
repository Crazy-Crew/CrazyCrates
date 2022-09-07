package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.BrokeLocation;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import com.badbones69.crazycrates.common.configuration.files.Config;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
@Singleton
public class BrokeLocationsListener implements Listener {

    @Inject
    private CrazyManager crazyManager;
    @Inject
    private CrazyLogger crazyLogger;

    @EventHandler(ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent e) {
        while (!crazyManager.getBrokeCrateLocations().isEmpty()) {
            int fixedAmount = 0;

            List<BrokeLocation> fixedWorlds = new ArrayList<>();

            for (BrokeLocation brokeLocation : crazyManager.getBrokeCrateLocations()) {
                Location location = brokeLocation.getLocation();

                if (location.getWorld() != null) {
                    crazyManager.getCrateLocations().add(new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location));

                    if (crazyManager.getHologramController() != null)
                        crazyManager.getHologramController().createHologram(location.getBlock(), brokeLocation.getCrate());

                    fixedWorlds.add(brokeLocation);
                    fixedAmount++;
                }
            }

            crazyManager.getBrokeCrateLocations().removeAll(fixedWorlds);

            if (Config.TOGGLE_VERBOSE) {
                crazyLogger.debug("<red>Fixed</red> <gold>" + fixedAmount + "</gold> <red>broken crate locations.</red>");

                if (crazyManager.getBrokeCrateLocations().isEmpty())
                    crazyLogger.debug("<red>All broken crate locations have been fixed.</red>");
            }
        }
    }
}