package com.badbones69.crazycrates.paper.metrics;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bstats.bukkit.Metrics;

public class MetricsWrapper {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    public void start() {
        new Metrics(this.plugin, 4514);

        //crazyManager.getCrates().forEach(crate -> {
       //     CrateType crateType = crate.getCrateType();

        //    SimplePie crateChart = new SimplePie("crate_types", crateType::getName);

        //    metrics.addCustomChart(crateChart);
        //});

        this.plugin.getLogger().info("Metrics has been enabled.");
    }
}