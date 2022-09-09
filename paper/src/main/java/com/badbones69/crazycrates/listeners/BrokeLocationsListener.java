package com.badbones69.crazycrates.listeners;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.utilities.LoggerUtils;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.CrateBrokeLocation;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.CrateLocation;
import com.badbones69.crazycrates.common.configuration.files.Config;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    private final CrazyManager crazyManager;

    private final LoggerUtils loggerUtils;

    public BrokeLocationsListener(CrazyManager crazyManager, LoggerUtils loggerUtils) {
        this.crazyManager = crazyManager;

        this.loggerUtils = loggerUtils;
    }

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
                loggerUtils.debug("<red>Fixed</red> <gold>" + fixedAmount + "</gold> <red>broken crate locations.</red>");

                if (crazyManager.getBrokeCrateLocations().isEmpty()) loggerUtils.debug("<red>All broken crate locations have been fixed.</red>");
            }
        }
    }
}