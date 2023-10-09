package us.crazycrew.crazycrates.paper.api.plugin;

import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.MetricsHandler;
import us.crazycrew.crazycrates.paper.api.plugin.migration.MigrationService;
import us.crazycrew.crazycrates.paper.misc.Methods;

public class CrazyHandler extends CrazyCratesPlugin {

    private final CrazyCrates plugin;

    public CrazyHandler(CrazyCrates plugin) {
        super(plugin.getDataFolder(), Platform.type.paper);

        this.plugin = plugin;
    }

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;
    private Methods methods;

    public void install() {
        // Enable cluster bukkit api
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        // Run migration checks
        MigrationService service = new MigrationService(this.plugin);
        service.migrate();

        // Enable crazycrates api
        super.enable();

        LegacyLogger.setName(getConfigManager().getPluginConfig().getProperty(PluginConfig.console_prefix));

        boolean metrics = this.plugin.getConfigManager().getPluginConfig().getProperty(PluginConfig.toggle_metrics);

        this.methods = new Methods(this.plugin);

        this.metrics = new MetricsHandler(this.plugin);
        if (metrics) this.metrics.start();
    }

    public void uninstall() {
        // Disable crazycrates api.
        super.disable();

        // Disable cluster bukkit api.
        this.bukkitPlugin.disable();
    }

    @Override
    @Nullable
    public String identifyClassLoader(ClassLoader classLoader) throws Exception {
        Class<?> classLoaderClass = Class.forName("org.bukkit.plugin.java.PluginClassLoader");

        if (classLoaderClass.isInstance(classLoader)) {
            return this.plugin.getName();
        }

        return null;
    }

    /**
     * Inherited methods.
     */
    @Override
    @NotNull
    public ConfigManager getConfigManager() {
        return super.getConfigManager();
    }

    @Override
    @NotNull
    public UserManager getUserManager() {
        return null;
    }

    @NotNull
    public MetricsHandler getMetrics() {
        return this.metrics;
    }

    public Methods getMethods() {
        return this.methods;
    }
}