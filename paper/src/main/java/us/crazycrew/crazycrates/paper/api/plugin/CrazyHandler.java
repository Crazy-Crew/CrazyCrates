package us.crazycrew.crazycrates.paper.api.plugin;

import com.ryderbelserion.cluster.paper.AbstractPaperPlugin;
import com.ryderbelserion.cluster.paper.files.FileManager;
import net.kyori.adventure.text.Component;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.platforms.Platform;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.paper.api.MetricsManager;
import us.crazycrew.crazycrates.paper.api.enums.Files;
import us.crazycrew.crazycrates.paper.crates.CrateManager;
import us.crazycrew.crazycrates.paper.crates.menus.GuiManager;

public class CrazyHandler extends CrazyCratesPlugin {

    private final CrazyCrates plugin;

    public CrazyHandler(CrazyCrates plugin) {
        super(plugin.getDataFolder(), Platform.type.paper);

        this.plugin = plugin;
    }

    private AbstractPaperPlugin paperPlugin;
    private FileManager fileManager;
    private CrateManager crateManager;
    private GuiManager guiManager;
    private MetricsManager metricsManager;

    public void install() {
        // Enable crazycrates api.
        super.enable();

        // Enable paper plugin api.
        this.paperPlugin = new AbstractPaperPlugin(this.plugin, getConfigManager().getPluginConfig().getProperty(PluginConfig.verbose_logging));
        this.paperPlugin.enable();

        // Load all the necessary files.
        this.fileManager = this.paperPlugin.getFileManager();
        this.fileManager
                .addStaticFile("locations.yml")
                .addStaticFile("users.yml")
                .addStaticFile("events.log")
                .addDynamicFile("crates", "CrateExample.yml")
                .addDynamicFile("crates", "WarCrateExample.yml")
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

        // Load guis.
        this.guiManager = new GuiManager(this.plugin);
        this.guiManager.load();

        // Start metrics.
        this.metricsManager = new MetricsManager(this.plugin);
        this.metricsManager.start();
    }

    public void reload() {
        getConfigManager().reload();

        // Reload all custom files.
        this.fileManager.reloadDynamicFiles();

        // Reload all static files.
        this.fileManager.reloadStaticFile(Files.users.getFileName());
        this.fileManager.reloadStaticFile(Files.locations.getFileName());

        // Re-populate everything.
        this.fileManager.create();

        // Reload the crates.
        this.crateManager.reload();

        // Reload the guis.
        this.guiManager.reload();
    }

    public void uninstall() {
        // Disable crazycrates api.
        super.disable();

        // Unload crates.
        this.crateManager.unload();

        // Stop metrics.
        this.metricsManager.stop();

        // Disable paper plugin api.
        this.paperPlugin.disable();
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

    @NotNull
    public GuiManager getGuiManager() {
        return this.guiManager;
    }

    @NotNull
    public Component parse(String message) {
        return this.paperPlugin.parse(message);
    }
}