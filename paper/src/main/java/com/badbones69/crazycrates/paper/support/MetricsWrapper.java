package com.badbones69.crazycrates.paper.support;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class MetricsWrapper{

    private final SettingsManager config = ConfigManager.getConfig();

    private final CrateManager crateManager;

    private final Metrics metrics;

    public MetricsWrapper(@NotNull final CrazyCrates plugin, final int serviceId) {
        this.metrics = new Metrics(plugin, serviceId);

        this.crateManager = plugin.getCrateManager();
    }

    public void start() {
        if (this.metrics == null || !this.config.getProperty(ConfigKeys.toggle_metrics)) return;

        final List<Crate> crates = new ArrayList<>(this.crateManager.getCrates());

        crates.forEach(crate -> {
            final CrateType type = crate.getCrateType();

            if (type == CrateType.menu) return;

            this.metrics.addCustomChart(new SimplePie("crate_types", type::getName));
        });
    }

    public @NotNull final Metrics getMetrics() {
        return this.metrics;
    }
}