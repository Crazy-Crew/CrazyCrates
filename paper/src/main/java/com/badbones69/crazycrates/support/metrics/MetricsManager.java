package com.badbones69.crazycrates.support.metrics;

import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class MetricsManager {

    @NotNull
    private final CrazyCratesPaper plugin = CrazyCratesPaper.get();

    private Metrics metrics;

    public void start() {
        if (this.metrics != null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().info("Metrics is already enabled.");

            return;
        }

        this.metrics = new Metrics(this.plugin, 4514);

        this.plugin.getCrateManager().getUsableCrates().forEach(crate -> {
            CrateType crateType = crate.getCrateType();

            // If the crate type is null. don't add to the pie chart.
            if (crateType == null) return;

            SimplePie chart = new SimplePie("crate_types", crateType::getName);

            this.metrics.addCustomChart(chart);
        });

        if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().info("Metrics isn't enabled so we do nothing.");

            return;
        }

        this.metrics.shutdown();

        this.metrics = null;

        if (MiscUtils.isLogging()) this.plugin.getLogger().warning("Metrics has been turned off.");
    }
}