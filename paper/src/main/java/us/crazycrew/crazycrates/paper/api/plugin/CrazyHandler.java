package us.crazycrew.crazycrates.paper.api.plugin;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.paper.api.MetricsHandler;
import java.io.File;

public class CrazyHandler extends CrazyCratesPlugin {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;
    private FileManager fileManager;

    private Methods methods;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void install() {
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        //super.enable();

        //LegacyLogger.setName(getConfigManager().getConfig().getProperty(Config.console_prefix));

        this.methods = new Methods();

        this.fileManager = new FileManager();
        this.fileManager
                .registerDefaultGenerateFiles("Example.yml", "/vouchers", "/vouchers")
                .registerDefaultGenerateFiles("Example-Arg.yml", "/vouchers", "/vouchers")
                .registerDefaultGenerateFiles("PlayerHead.yml", "/vouchers", "/vouchers")
                .registerDefaultGenerateFiles("Starter-Money.yml", "/codes", "/codes")
                .registerCustomFilesFolder("/vouchers")
                .registerCustomFilesFolder("/codes")
                .setup();

        //boolean metrics = getConfigManager().getConfig().getProperty(Config.toggle_metrics);

        //this.metrics = new MetricsHandler();
        //if (metrics) this.metrics.start();
    }

    public void uninstall() {
        // Disable crazyenvoys api.
        //super.disable();

        // Disable cluster bukkit api.
        this.bukkitPlugin.disable();
    }

    /**
     * Inherited methods.
     */
    @Override
    public @NotNull ConfigManager getConfigManager() {
        return super.getConfigManager();
    }

    public @NotNull Methods getMethods() {
        return this.methods;
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull MetricsHandler getMetrics() {
        return this.metrics;
    }
}