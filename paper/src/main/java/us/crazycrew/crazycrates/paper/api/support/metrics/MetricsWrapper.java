package us.crazycrew.crazycrates.paper.api.support.metrics;

import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class MetricsWrapper {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

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