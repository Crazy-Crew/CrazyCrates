package com.badbones69.crazycrates.platform;

import com.badbones69.crazycrates.api.Settings;
import com.ryderbelserion.vital.common.AbstractPlugin;
import com.ryderbelserion.vital.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import us.crazycrew.crazycrates.platform.IServer;
import com.badbones69.crazycrates.platform.config.ConfigManager;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class Server extends AbstractPlugin implements IServer {

    private final FileManager fileManager;
    private final JavaPlugin plugin;
    private final File crateFolder;

    private Settings settings;
    private UserManager userManager;

    @ApiStatus.Internal
    public Server(@NotNull final JavaPlugin plugin) {
        super(plugin.getName());

        this.plugin = plugin;

        this.crateFolder = new File(this.plugin.getDataFolder(), "crates");
        this.fileManager = new FileManager(getDirectory(), getLogger());
    }

    @ApiStatus.Internal
    public void enable() {
        ConfigManager.load(this.plugin.getDataFolder());

        this.settings = new Settings();

        this.fileManager.addDefaultFile("crates", "CrateExample.yml")
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
                .addFolder("schematics").apply();

        // Register default provider.
        CratesProvider.register(this);
    }

    @ApiStatus.Internal
    public void disable() {
        // Unregister default provider.
        CratesProvider.unregister();
    }

    @ApiStatus.Internal
    public @NotNull final FileManager getFileManager() {
        return this.fileManager;
    }

    @ApiStatus.Internal
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public void reload() {
        ConfigManager.reload();
    }

    @Override
    public @NotNull final Path getDirectory() {
        return this.plugin.getDataFolder().toPath();
    }

    @Override
    public @NotNull final Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public @NotNull final File getCrateFolder() {
        return this.crateFolder;
    }

    @Override
    public @NotNull final File[] getCrateFiles() {
        return this.crateFolder.listFiles((dir, name) -> name.endsWith(".yml"));
    }

    @Override
    public @NotNull final UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public @NotNull ISettings getSettings() {
        return this.settings;
    }
}