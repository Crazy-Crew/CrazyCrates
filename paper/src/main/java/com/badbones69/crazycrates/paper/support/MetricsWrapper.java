package com.badbones69.crazycrates.paper.support;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.config.impl.types.plugin.PluginConfig;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class MetricsWrapper{

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final ConfigManager configManager = this.platform.getConfigManager();
    private final PluginConfig pluginConfig = this.configManager.getPluginConfig();

    private final CrateManager crateManager = this.platform.getCrateManager();

    private final Metrics metrics;

    public MetricsWrapper(final int serviceId) {
        this.metrics = new Metrics(this.plugin, serviceId);
    }

    public void start() {
        if (!this.pluginConfig.isMetricsEnabled()) return;

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