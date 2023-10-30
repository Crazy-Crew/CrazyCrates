package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.api.FileManager;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.MigrationService;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import us.crazycrew.crazycrates.paper.api.crates.PrizeManager;
import us.crazycrew.crazycrates.paper.api.support.metrics.MetricsWrapper;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;
import us.crazycrew.crazycrates.paper.support.structures.blocks.ChestStateHandler;
import java.io.File;

public class CrazyHandler extends CrazyCratesPlugin {

    private FileManager fileManager;

    private CrateManager crateManager;
    private PrizeManager prizeManager;

    private ChestStateHandler chestStateHandler;

    private BukkitUserManager userManager;

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
        this.prizeManager = new PrizeManager();

        this.chestStateHandler = new ChestStateHandler();

        // Creates user manager instance.
        this.userManager = new BukkitUserManager();

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
    public ChestStateHandler getChestStateHandler() {
        return this.chestStateHandler;
    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    @NotNull
    public MetricsWrapper getMetrics() {
        return this.metrics;
    }
}