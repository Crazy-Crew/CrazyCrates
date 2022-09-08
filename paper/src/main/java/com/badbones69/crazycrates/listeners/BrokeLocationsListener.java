package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.CrateBrokeLocation;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.CrateLocation;
import com.badbones69.crazycrates.common.configuration.files.Config;
import com.badbones69.crazycrates.api.utilities.logger.CrazyLogger;
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

            List<CrateBrokeLocation> fixedWorlds = new ArrayList<>();

            for (CrateBrokeLocation crateBrokeLocation : crazyManager.getBrokeCrateLocations()) {
                Location location = crateBrokeLocation.getLocation();

                if (location.getWorld() != null) {
                    crazyManager.getCrateLocations().add(new CrateLocation(crateBrokeLocation.getLocationName(), crateBrokeLocation.getCrate(), location));

                    if (crazyManager.getHologramController() != null)
                        crazyManager.getHologramController().createHologram(location.getBlock(), crateBrokeLocation.getCrate());

                    fixedWorlds.add(crateBrokeLocation);
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