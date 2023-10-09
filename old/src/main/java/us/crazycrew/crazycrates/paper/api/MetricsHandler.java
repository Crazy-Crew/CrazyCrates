package us.crazycrew.crazycrates.paper.api;

import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.bstats.bukkit.Metrics;
import us.crazycrew.crazycrates.paper.CrazyCrates;

public class MetricsHandler {

    private final CrazyCrates plugin;

    public MetricsHandler(CrazyCrates plugin) {
        this.plugin = plugin;
    }

    private Metrics metrics;

    public void start() {
        if (this.metrics != null) {
            if (this.plugin.isLogging()) LegacyLogger.warn("Metrics is already enabled.");
            return;
        }

        this.metrics = new Metrics(this.plugin, 4536);

        if (this.plugin.isLogging()) LegacyLogger.success("Metrics has been enabled.");
    }

    public void stop() {
        if (this.metrics == null) {
            if (this.plugin.isLogging()) LegacyLogger.warn("Metrics isn't enabled so we do nothing.");
            return;
        }

        this.metrics.shutdown();
        this.metrics = null;

        if (this.plugin.isLogging()) LegacyLogger.success("Metrics has been turned off.");
    }
}