package us.crazycrew.crazycrates.paper.api.plugin;

import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.cluster.bukkit.BukkitPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.paper.crates.CrateManager;
import us.crazycrew.crazycrates.paper.misc.FileManager;

public class CrazyHandler extends CrazyCratesPlugin {

    private final CrazyCrates plugin;

    public CrazyHandler(CrazyCrates plugin) {
        super(plugin.getDataFolder(), Platform.type.paper);

        this.plugin = plugin;
    }

    private BukkitPlugin bukkitPlugin;
    private FileManager fileManager;
    private CrateManager crateManager;

    public void install() {
        // Enable cluster bukkit api.
        this.bukkitPlugin = new BukkitPlugin(this.plugin);
        this.bukkitPlugin.enable();

        // Enable crazycrates api.
        super.enable(this.plugin.getServer());

        // Load all the necessary files.
        this.fileManager = new FileManager(this.plugin);
        this.fileManager
                .addStaticFile("locations.yml")
                .addStaticFile("users.yml")
                .addStaticFile("events.log")
                .addDynamicFile("crates", "CrateExample.yml")
                //.addDynamicFile("crates", "QuadCrateExample.yml")
                //.addDynamicFile("crates", "QuickCrateExample.yml")
                //.addDynamicFile("crates", "CosmicCrateExample.yml")
                .addDynamicFile("schematics", "classic.nbt")
                .addDynamicFile("schematics", "nether.nbt")
                .addDynamicFile("schematics", "outdoors.nbt")
                .addDynamicFile("schematics", "sea.nbt")
                .addDynamicFile("schematics", "soul.nbt")
                .addDynamicFile("schematics", "wooden.nbt")
                .addFolder("crates")
                .addFolder("schematics")
                .create();

        // Load crates.
        this.crateManager = new CrateManager(this.plugin);
        this.crateManager.load();
    }

    public void uninstall() {
        // Disable crazycrates api.
        super.disable();

        // Unload crates.
        this.crateManager.unload();

        // Disable cluster bukkit api.
        this.bukkitPlugin.disable();
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

    /**
     * Internal methods.
     */
    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @NotNull
    public CrateManager getCrateManager() {
        return this.crateManager;
    }
}