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
import us.crazycrew.crazycrates.paper.api.plugin.migration.MigrationService;
import us.crazycrew.crazycrates.paper.misc.FileManager;

public class CrazyHandler extends CrazyCratesPlugin {

    private final CrazyCrates plugin;

    public CrazyHandler(CrazyCrates plugin) {
        super(plugin.getDataFolder(), Platform.type.paper);

        this.plugin = plugin;
    }

    private BukkitPlugin bukkitPlugin;
    private FileManager fileManager;

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

        this.fileManager = new FileManager(this.plugin);
        this.fileManager
                .addStaticFile("data.yml")
                .addStaticFile("events.log")
                .addStaticFile("locations.yml")
                .addDynamicFile("crates", "CrateExample.yml")
                .addDynamicFile("crates", "QuadCrateExample.yml")
                .addDynamicFile("crates", "QuickCrateExample.yml")
                .addDynamicFile("crates", "CosmicCrateExample.yml")
                .addDynamicFile("schematics", "classic.nbt")
                .addDynamicFile("schematics", "nether.nbt")
                .addDynamicFile("schematics", "outdoors.nbt")
                .addDynamicFile("schematics", "sea.nbt")
                .addDynamicFile("schematics", "soul.nbt")
                .addDynamicFile("schematics", "wooden.nbt")
                .addFolder("crates")
                .addFolder("schematics")
                .create();
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

    /**
     * Internal methods.
     */
    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }
}