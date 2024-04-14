package us.crazycrew.crazycrates.platform;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCratesService;
import us.crazycrew.crazycrates.api.ICrazyCrates;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import java.io.File;
import java.util.logging.Logger;

public class Server implements ICrazyCrates {

    private final JavaPlugin plugin;
    private final File crateFolder;

    public Server(JavaPlugin plugin) {
        this.plugin = plugin;

        this.crateFolder = new File(this.plugin.getDataFolder(), "crates");
    }

    public void enable() {
        ConfigManager.load(this.plugin.getDataFolder());

        // Register legacy provider.
        CrazyCratesService.register(this);

        // Register default provider.
        CratesProvider.register(this);
    }

    public void reload() {
        ConfigManager.reload();
    }

    public void disable() {
        // Unregister legacy provider.
        CrazyCratesService.unregister();

        // Unregister default provider.
        CratesProvider.unregister();
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

    private UserManager userManager;

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public @NotNull UserManager getUserManager() {
        return this.userManager;
    }
}