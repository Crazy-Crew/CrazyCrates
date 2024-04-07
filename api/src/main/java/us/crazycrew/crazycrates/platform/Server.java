package us.crazycrew.crazycrates.platform;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.platform.config.ConfigManager;
import java.io.File;
import java.util.logging.Logger;

public class Server {

    private final JavaPlugin plugin;
    private final File crateFolder;

    public Server(JavaPlugin plugin) {
        this.plugin = plugin;

        this.crateFolder = new File(this.plugin.getDataFolder(), "crates");
    }

    public void enable() {
        ConfigManager.load(this.plugin.getDataFolder());

        CratesProvider.register(this);
    }

    public void reload() {
        ConfigManager.reload();
    }

    public void disable() {
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
}