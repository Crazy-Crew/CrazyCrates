package com.badbones69.crazycrates.paper.support;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class MetricsWrapper{

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager;

    private final Metrics metrics;

    public MetricsWrapper(final int serviceId) {
        this.metrics = new Metrics(this.plugin, serviceId);

        this.crateManager = this.plugin.getCrateManager();
    }

    public void start() {
        if (this.metrics == null || !ConfigManager.getConfig().getProperty(ConfigKeys.toggle_metrics)) return;

        final List<Crate> crates = new ArrayList<>(this.crateManager.getCrates());

        crates.forEach(crate -> {
            CrateType type = crate.getCrateType();

            if (type == null || type == CrateType.menu) return;

            SimplePie chart = new SimplePie("crate_types", type::getName);

            this.metrics.addCustomChart(chart);
        });
    }

    public final Metrics getMetrics() {
        return this.metrics;
    }
}