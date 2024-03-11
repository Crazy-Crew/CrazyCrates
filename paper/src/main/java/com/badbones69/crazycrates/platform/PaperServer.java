package com.badbones69.crazycrates.platform;

import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.platform.Server;
import java.io.File;

public class PaperServer implements Server, ICrazyCrates {

    @NotNull
    private final CrazyCratesPaper plugin = JavaPlugin.getPlugin(CrazyCratesPaper.class);

    private final InventoryManager inventoryManager;
    private final BukkitUserManager userManager;
    private final CrateManager crateManager;
    private final FileManager fileManager;

    public PaperServer() {
        // The file manager is depended on by the user manager.
        this.fileManager = new FileManager();

        //todo() config files are needed for inventory manager.
        this.inventoryManager = new InventoryManager();

        //todo() the inventory manager has to be loaded before this.
        this.crateManager = new CrateManager();

        // The user manager depends on the file manager.
        //todo() config files and crate manager has to be loaded before this.
        this.userManager = new BukkitUserManager();
    }

    @Override
    public File getFolder() {
        return this.plugin.getDataFolder();
    }

    @NotNull
    @Override
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public InventoryManager getInventoryManager() {
        return this.inventoryManager;
    }

    public CrateManager getCrateManager() {
        return this.crateManager;
    }
}