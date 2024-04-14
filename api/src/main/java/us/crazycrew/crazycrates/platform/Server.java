package us.crazycrew.crazycrates.platform;

import com.ryderbelserion.vital.common.AbstractPlugin;
import com.ryderbelserion.vital.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class Server extends AbstractPlugin implements ICrazyCrates {

    private FileManager fileManager;
    private final JavaPlugin plugin;
    private final File crateFolder;

    @ApiStatus.Internal
    public Server(JavaPlugin plugin) {
        super(plugin.getName());

        this.plugin = plugin;

        this.crateFolder = new File(this.plugin.getDataFolder(), "crates");
    }

    @ApiStatus.Internal
    public void enable() {
        ConfigManager.load(this.plugin.getDataFolder());
      
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

        // Register legacy provider.
        CrazyCratesService.register(this);

        // Register default provider.
        CratesProvider.register(this);
    }

    public void reload() {
        ConfigManager.reload();
    }

    @ApiStatus.Internal
    public void disable() {
        // Unregister legacy provider.
        CrazyCratesService.unregister();

        // Unregister default provider.
        CratesProvider.unregister();
    }

    @Override
    public @NotNull Path getDirectory() {
        return this.plugin.getDataFolder().toPath();
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.plugin.getLogger();
    }

    public @NotNull FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull File getCrateFolder() {
        return this.crateFolder;
    }

    public @NotNull File[] getCrateFiles() {
        return this.crateFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    private UserManager userManager;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public @NotNull UserManager getUserManager() {
        return this.userManager;
    }
}