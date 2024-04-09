package us.crazycrew.crazycrates.platform;

import com.ryderbelserion.vital.VitalPlugin;
import com.ryderbelserion.vital.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import java.io.File;
import java.util.logging.Logger;

public class Server {

    private FileManager fileManager;
    private final JavaPlugin plugin;
    private final File crateFolder;

    private final VitalPlugin vital;

    public Server(JavaPlugin plugin) {
        this.plugin = plugin;

        this.crateFolder = new File(this.plugin.getDataFolder(), "crates");
        this.vital = new VitalPlugin(plugin);
    }

    public void enable() {
        this.vital.start();

        ConfigManager.load(this.plugin.getDataFolder());

        this.vital.setLogging(ConfigManager.getConfig().getProperty(ConfigKeys.verbose_logging));

        this.fileManager = new FileManager();
        this.fileManager
                .addDefaultFile("crates", "CrateExample.yml")
                .addDefaultFile("crates", "QuadCrateExample.yml")
                .addDefaultFile("crates", "QuickCrateExample.yml")
                .addDefaultFile("crates", "WarCrateExample.yml")
                .addDefaultFile("crates", "CosmicCrateExample.yml")
                .addDefaultFile("crates", "CasinoExample.yml")
                .addDefaultFile("schematics", "classic.nbt")
                .addDefaultFile("schematics", "nether.nbt")
                .addDefaultFile("schematics", "outdoors.nbt")
                .addDefaultFile("schematics", "sea.nbt")
                .addDefaultFile("schematics", "soul.nbt")
                .addDefaultFile("schematics", "wooden.nbt")
                .addStaticFile("locations.yml")
                .addStaticFile("data.yml")
                .addFolder("crates")
                .addFolder("schematics").create();

        CratesProvider.register(this);
    }

    public void reload() {
        ConfigManager.reload();
    }

    public void disable() {
        if (this.vital != null) {
            this.vital.stop();
        }

        CratesProvider.unregister();
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull File getFolder() {
        return this.plugin.getDataFolder();
    }

    public @NotNull Logger getLogger() {
        return this.plugin.getLogger();
    }

    public @NotNull File getCrateFolder() {
        return this.crateFolder;
    }

    public @NotNull File[] getCrateFiles() {
        return this.crateFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }
}