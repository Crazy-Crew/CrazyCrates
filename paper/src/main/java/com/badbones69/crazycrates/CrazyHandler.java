package com.badbones69.crazycrates;

import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.commands.CommandManager;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.CrazyCratesPlugin;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.support.metrics.MetricsManager;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.api.utils.FileUtils;

public class CrazyHandler extends CrazyCratesPlugin {

    public CrazyHandler(CrazyCrates plugin) {
        super(plugin.getDataFolder());
    }

    private InventoryManager inventoryManager;
    private BukkitUserManager userManager;
    private CrateManager crateManager;
    private FileManager fileManager;
    private MetricsManager metrics;

    public void load() {
        super.enable();

        // Load all files.
        this.fileManager = new FileManager();
        this.fileManager.registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("WarCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CasinoExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                .registerCustomFilesFolder("/crates")
                .registerCustomFilesFolder("/schematics")
                .setup();

        FileUtils.loadFiles();

        // Create crate manager.
        this.crateManager = new CrateManager();

        // Create inventory manager.
        this.inventoryManager = new InventoryManager();

        this.crateManager.loadCrates();

        // Creates user manager instance.
        this.userManager = new BukkitUserManager();

        // Load commands.
        CommandManager commandManager = new CommandManager();
        commandManager.load();

        // Load metrics.
        boolean metrics = getConfigManager().getConfig().getProperty(ConfigKeys.toggle_metrics);

        this.metrics = new MetricsManager();
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
    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    @NotNull
    public CrateManager getCrateManager() {
        return this.crateManager;
    }

    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @NotNull
    public MetricsManager getMetrics() {
        return this.metrics;
    }
}