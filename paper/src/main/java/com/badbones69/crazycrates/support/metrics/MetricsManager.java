package com.badbones69.crazycrates.support.metrics;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class MetricsManager {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

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

        if (MiscUtils.isLogging()) this.plugin.getLogger().info("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (MiscUtils.isLogging()) this.plugin.getLogger().info("Metrics isn't enabled so we do nothing.");

            return;
        }

        this.metrics.shutdown();

        this.metrics = null;

        if (MiscUtils.isLogging()) this.plugin.getLogger().info("Metrics has been turned off.");
    }
}