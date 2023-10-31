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
import us.crazycrew.crazycrates.paper.commands.CommandManager;
import java.io.File;

public class CrazyHandler extends CrazyCratesPlugin {

    private FileManager fileManager;

    private CrateManager crateManager;
    private PrizeManager prizeManager;

    private ChestManager chestManager;

    private BukkitUserManager userManager;
    private EventLogger eventLogger;

    private MetricsWrapper metrics;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void load() {
        super.enable();

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

        this.crateManager = new CrateManager();
        this.crateManager.loadCrates();

        this.prizeManager = new PrizeManager();

        this.chestManager = new ChestManager();

        // Creates user manager instance.
        this.userManager = new BukkitUserManager();

        this.eventLogger = new EventLogger();

        CommandManager commandManager = new CommandManager();
        commandManager.load();

        // Migrates 2 config.yml settings to plugin-config.yml
        MigrationService service = new MigrationService();
        service.migrate();

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
    public ChestManager getChestManager() {
        return this.chestManager;
    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    public EventLogger getEventLogger() {
        return this.eventLogger;
    }

    @NotNull
    public MetricsWrapper getMetrics() {
        return this.metrics;
    }
}