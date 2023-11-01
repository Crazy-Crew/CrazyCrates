package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.MigrationService;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import us.crazycrew.crazycrates.paper.api.crates.PrizeManager;
import us.crazycrew.crazycrates.paper.api.support.metrics.MetricsWrapper;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;
import us.crazycrew.crazycrates.paper.api.support.structures.blocks.ChestManager;
import us.crazycrew.crazycrates.paper.api.users.guis.InventoryManager;
import us.crazycrew.crazycrates.paper.commands.CommandManager;
import us.crazycrew.crazycrates.paper.modules.ModuleLoader;

public class CrazyHandler extends CrazyCratesPlugin {

    public CrazyHandler(CrazyCrates plugin) {
        super(plugin.getDataFolder());
    }

    private FileManager fileManager;

    private CrateManager crateManager;
    private PrizeManager prizeManager;

    private InventoryManager inventoryManager;

    private ModuleLoader moduleLoader;

    private ChestManager chestManager;

    private BukkitUserManager userManager;
    private EventLogger eventLogger;

    private MetricsWrapper metrics;

    public void load() {
        super.enable();

        // Load all files.
        this.fileManager = new FileManager();
        this.fileManager.registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
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

        // Load crates.
        this.crateManager = new CrateManager();

        // Create inventory manager.
        this.inventoryManager = new InventoryManager();

        this.crateManager.loadCrates();

        // Enable prizes.
        this.prizeManager = new PrizeManager();

        // Create chest manager.
        this.chestManager = new ChestManager();

        // Creates user manager instance.
        this.userManager = new BukkitUserManager();

        // Create event logger.
        this.eventLogger = new EventLogger();

        // Load commands.
        CommandManager commandManager = new CommandManager();
        commandManager.load();

        this.moduleLoader = new ModuleLoader();
        this.moduleLoader.load();

        // Migrates 2 config.yml settings to plugin-config.yml.
        MigrationService service = new MigrationService();
        service.migrate();

        // Load metrics.
        boolean metrics = getConfigManager().getPluginConfig().getProperty(PluginConfig.toggle_metrics);

        this.metrics = new MetricsWrapper();
        if (metrics) this.metrics.start();
    }

    public void unload() {
        super.disable();
    }

    public void cleanFiles() {
        if (!Files.LOCATIONS.getFile().contains("Locations")) {
            Files.LOCATIONS.getFile().set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        if (!Files.DATA.getFile().contains("Players")) {
            Files.DATA.getFile().set("Players.Clear", null);
            Files.DATA.saveFile();
        }
    }

    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @NotNull
    public CrateManager getCrateManager() {
        return this.crateManager;
    }

    @NotNull
    public PrizeManager getPrizeManager() {
        return this.prizeManager;
    }

    @NotNull
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @NotNull
    public ChestManager getChestManager() {
        return this.chestManager;
    }

    @NotNull
    public EventLogger getEventLogger() {
        return this.eventLogger;
    }

    @NotNull
    public ModuleLoader getModuleLoader() {
        return this.moduleLoader;
    }

    @NotNull
    public MetricsWrapper getMetrics() {
        return this.metrics;
    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }
}