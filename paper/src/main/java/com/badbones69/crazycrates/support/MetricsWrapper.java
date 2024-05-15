package com.badbones69.crazycrates.support;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CustomMetrics;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class MetricsWrapper extends CustomMetrics {

    private final CrateManager crateManager;

    /**
     * Creates a new Metrics instance.
     *
     * @param serviceId The id of the service. It can be found at <a href="https://bstats.org/what-is-my-plugin-id">What is my plugin id?</a>
     */
    public MetricsWrapper(CrazyCrates plugin, int serviceId) {
        super(plugin, serviceId);

        this.crateManager = plugin.getCrateManager();
    }

    public void start() {
        List<Crate> crates = new ArrayList<>(this.crateManager.getCrates());

        crates.forEach(crate -> {
            CrateType type = crate.getCrateType();

            if (type == null || type == CrateType.menu) return;

            SimplePie chart = new SimplePie("crate_types", type::getName);

            addCustomChart(chart);
        });
    }
}