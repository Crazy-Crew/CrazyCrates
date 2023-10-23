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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

// Only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    @NotNull
    private final FileManager fileManager = this.plugin.getStarter().getFileManager();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!this.crazyManager.getBrokeCrateLocations().isEmpty()) {
            int fixedAmount = 0;
            List<BrokeLocation> fixedWorlds = new ArrayList<>();

            for (BrokeLocation brokeLocation : this.crazyManager.getBrokeCrateLocations()) {
                Location location = brokeLocation.getLocation();

                if (location.getWorld() != null) {
                    if (brokeLocation.getCrate() != null) {
                        this.crazyManager.getCrateLocations().add(new CrateLocation(brokeLocation.getLocationName(), brokeLocation.getCrate(), location));

                        if (brokeLocation.getCrate().getHologram().isEnabled() && this.crazyManager.getHologramController() != null) this.crazyManager.getHologramController().createHologram(location.getBlock(), brokeLocation.getCrate());

                        fixedWorlds.add(brokeLocation);
                        fixedAmount++;
                    }
                }
            }

            this.crazyManager.getBrokeCrateLocations().removeAll(fixedWorlds);

            if (this.fileManager.isLogging()) {
                this.plugin.getLogger().warning("Fixed " + fixedAmount + " broken crate locations.");

                if (this.crazyManager.getBrokeCrateLocations().isEmpty()) this.plugin.getLogger().warning("All broken crate locations have been fixed.");
            }
        }
    }
}