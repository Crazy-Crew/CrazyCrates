package com.badbones69.crazycrates.paper.support;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bstats.bukkit.Metrics;

public class MetricsHandler {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public void start() {
        new Metrics(plugin, 4514);

        //crazyManager.getCrates().forEach(crate -> {
       //     CrateType crateType = crate.getCrateType();

        //    SimplePie crateChart = new SimplePie("crate_types", crateType::getName);

        //    metrics.addCustomChart(crateChart);
        //});

        LegacyLogger.info("Metrics has been enabled.");
    }
}