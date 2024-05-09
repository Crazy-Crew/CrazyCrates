package com.badbones69.crazycrates.platform;

import com.badbones69.crazycrates.api.Settings;
import com.ryderbelserion.vital.common.AbstractPlugin;
import com.ryderbelserion.vital.common.util.FileUtil;
import com.ryderbelserion.vital.files.yaml.FileManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import us.crazycrew.crazycrates.platform.IServer;
import com.badbones69.crazycrates.platform.config.ConfigManager;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class Server extends AbstractPlugin implements IServer {

    private final FileManager fileManager;
    private final File crateFolder;
    private final File dataFolder;
    private final Logger logger;

    private UserManager userManager;
    private Settings settings;

    public Server(String pluginName, File dataFolder, Logger logger) {
        super(pluginName);

        this.dataFolder = dataFolder;
        this.logger = logger;

        this.crateFolder = new File(dataFolder, "crates");
        this.fileManager = new FileManager(getDirectory(), getLogger());
    }

    public void enable() {
        ConfigManager.load(this.dataFolder);

        this.settings = new Settings();

        this.fileManager.addDefaultFile("crates", "CrateExample.yml").addDefaultFile("crates", "AdvancedExample.yml")
                .addDefaultFile("crates/types", "CosmicCrateExample.yml")
                .addDefaultFile("crates/types", "QuickCrateExample.yml")
                .addDefaultFile("crates/types", "QuadCrateExample.yml")
                .addDefaultFile("crates/types", "WarCrateExample.yml")
                .addDefaultFile("crates/types", "CasinoExample.yml")
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

    public void disable() {
        // Unregister default provider.
        CratesProvider.unregister();
    }

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
        return this.dataFolder.toPath();
    }

    @Override
    public @NotNull final Logger getLogger() {
        return this.logger;
    }

    @Override
    public @NotNull final File getCrateFolder() {
        return this.crateFolder;
    }

    @Override
    public @NotNull final List<String> getCrateFiles() {
        return FileUtil.getFiles(this.crateFolder.toPath(), "crates", "yml", true);
    }

    @Override
    public @NotNull final UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public @NotNull final ISettings getSettings() {
        return this.settings;
    }
}