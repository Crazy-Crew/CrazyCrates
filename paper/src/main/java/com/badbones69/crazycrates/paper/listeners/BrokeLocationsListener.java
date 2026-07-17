package com.badbones69.crazycrates.paper.listeners;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;

// The only use for this class is to check if for broken locations and to try and fix them when the server loads the world.
public class BrokeLocationsListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final CrateManager crateManager = this.platform.getCrateManager();
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        /*final List<CrazyLocation> locations = this.crateManager.getBrokenLocations();

        if (locations.isEmpty()) return;

        int fixedAmount = 0;

        final List<CrazyLocation> fixedWorlds = new ArrayList<>();

        for (final CrazyLocation index : locations) {
            final Location location = index.getLocation();
            final World world = location.getWorld();

            if (world == null) continue;

            final Crate crate = index.getCrate();

            if (crate == null) continue;

            final CrateLocation crateLocation = new CrateLocation(index.getId(), crate, location);

            this.crateManager.addLocation(crateLocation);

            final HologramManager manager = this.crateManager.getHolograms();

            if (manager != null && crate.getHologram().isEnabled()) {
                manager.createHologram(location, crate, crateLocation.getID());
            }

            fixedWorlds.add(index);

            fixedAmount++;
        }

        this.crateManager.removeBrokeLocation(fixedWorlds);

        if (this.fusion.isVerbose()) {
            this.logger.warn("Fixed {} broken crate locations.", fixedAmount);

            if (this.crateManager.getBrokenLocations().isEmpty()) this.logger.warn("All broken crate locations have been fixed.");
        }*/
    }
}