package com.badbones69.crazycrates.controllers;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.enums.BrokeLocation;
import com.badbones69.crazycrates.api.objects.CrateLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
 *
 */
public class BrokeLocationsControl implements Listener {
    
    private CrazyManager cc = CrazyManager.getInstance();
    private FileManager fileManager = FileManager.getInstance();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        if (!cc.getBrokeCrateLocations().isEmpty()) {
            int fixedAmount = 0;
            List<BrokeLocation> fixedWorlds = new ArrayList<>();
            for (BrokeLocation brokeLocation : cc.getBrokeCrateLocations()) {
                Location location = brokeLocation.getLocation();
                if (location.getWorld() != null) {
                    cc.getCrateLocations().add(new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location));
                    if (cc.getHologramController() != null) {
                        cc.getHologramController().createHologram(location.getBlock(), brokeLocation.getCrate());
                    }
                    fixedWorlds.add(brokeLocation);
                    fixedAmount++;
                }
            }
            cc.getBrokeCrateLocations().removeAll(fixedWorlds);
            if (fileManager.isLogging()) {
                CrazyManager.getJavaPlugin().getLogger().warning(fileManager.getPrefix() + "Fixed " + fixedAmount + " broken crate locations.");
                if (cc.getBrokeCrateLocations().isEmpty()) {
                    CrazyManager.getJavaPlugin().getLogger().warning(fileManager.getPrefix() + "All broken crate locations have been fixed.");
                }
            }
        }
    }
}