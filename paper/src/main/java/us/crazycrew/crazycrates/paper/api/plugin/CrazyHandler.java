package us.crazycrew.crazycrates.paper.api.plugin;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.MetricsHandler;
import us.crazycrew.crazycrates.paper.api.plugin.migration.MigrationService;
import java.io.File;

public class CrazyHandler extends CrazyCratesPlugin {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;
    private FileManager fileManager;

    private Methods methods;

    public CrazyHandler(File dataFolder) {
        super(dataFolder, Platform.type.paper);
    }

    public void install() {
        // Enable cluster bukkit api
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        // Run migration checks
        // The migration check will only stay until 2.5
        MigrationService service = new MigrationService();
        service.migrate();

        // Enable crazycrates api
        super.enable(this.plugin.getServer().getConsoleSender());

        //LegacyLogger.setName(getConfigManager().getConfig().getProperty(Config.console_prefix));

        this.methods = new Methods();

        this.fileManager = new FileManager();
        this.fileManager.setLog(true)
                .registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                .registerCustomFilesFolder("/crates")
                .registerCustomFilesFolder("/schematics")
                .setup();

        boolean metrics = this.plugin.getCrazyHandler().getConfigManager().getPluginConfig().getProperty(PluginConfig.toggle_metrics);

        this.metrics = new MetricsHandler();
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

    /**
     * Internal methods.
     */
    @NotNull
    public Methods getMethods() {
        return this.methods;
    }

    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @NotNull
    public MetricsHandler getMetrics() {
        return this.metrics;
    }
}