package com.badbones69.crazycrates.support;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import org.bstats.bukkit.Metrics;

public class MetricsHandler {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    public void start() {
        Metrics metrics = new Metrics(plugin, 4514);

        //crazyManager.getCrates().forEach(crate -> {
       //     CrateType crateType = crate.getCrateType();

        //    SimplePie crateChart = new SimplePie("crate_types", crateType::getName);

        //    metrics.addCustomChart(crateChart);
        //});

        plugin.getLogger().info("Metrics has been enabled.");
    }
}