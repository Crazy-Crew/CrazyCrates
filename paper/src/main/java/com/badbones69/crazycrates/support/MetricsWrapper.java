package com.badbones69.crazycrates.support;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.api.bStats;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class MetricsWrapper extends bStats {

    private final CrateManager crateManager;

    public MetricsWrapper(@NotNull final CrazyCrates plugin, final int serviceId, final boolean isPaperPlugin) {
        super(plugin, serviceId, isPaperPlugin);

        this.crateManager = plugin.getCrateManager();
    }

    public void start() {
        final List<Crate> crates = new ArrayList<>(this.crateManager.getCrates());

        crates.forEach(crate -> {
            CrateType type = crate.getCrateType();

            if (type == null || type == CrateType.menu) return;

            SimplePie chart = new SimplePie("crate_types", type::getName);

            addCustomChart(chart);
        });
    }
}