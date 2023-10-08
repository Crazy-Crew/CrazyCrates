package us.crazycrew.crazycrates.paper.api.plugin;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.managers.MenuManager;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import com.badbones69.crazycrates.paper.support.structures.blocks.ChestManager;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
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

    private BukkitUserManager userManager;

    private BukkitPlugin bukkitPlugin;
    private MetricsHandler metrics;

    private ChestManager chestManager;
    private CrazyManager crazyManager;
    private FileManager fileManager;
    private MenuManager menuManager;
    private EventLogger eventLogger;

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
        super.enable();

        LegacyLogger.setName(getConfigManager().getPluginConfig().getProperty(PluginConfig.console_prefix));

        this.methods = new Methods();

        this.fileManager = new FileManager();
        this.fileManager
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

        this.crazyManager = new CrazyManager();
        this.userManager = new BukkitUserManager();

        this.crazyManager.load(true);

        this.menuManager = new MenuManager();
        this.menuManager.loadButtons();

        this.metrics = new MetricsHandler();
        if (metrics) this.metrics.start();

        this.chestManager = new ChestManager();
        this.eventLogger = new EventLogger();
    }

    public static void janitor() {
        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }
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
    public BukkitUserManager getUserManager() {
        return this.userManager;
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
    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
    @NotNull
    public MenuManager getMenuManager() {
        return this.menuManager;
    }

    @NotNull
    public MetricsHandler getMetrics() {
        return this.metrics;
    }

    @NotNull
    public ChestManager getChestManager() {
        return this.chestManager;
    }

    @NotNull
    public EventLogger getEventLogger() {
        return this.eventLogger;
    }
}