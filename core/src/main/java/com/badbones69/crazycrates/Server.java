package com.badbones69.crazycrates;

import com.ryderbelserion.vital.core.AbstractPlugin;
import com.ryderbelserion.vital.core.util.FileUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import us.crazycrew.crazycrates.platform.IServer;
import com.badbones69.crazycrates.config.ConfigManager;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class Server extends AbstractPlugin implements IServer {

    private final File directory;
    private final Logger logger;
    private final File crates;

    private UserManager userManager;
    private Settings settings;

    public Server(@NotNull final File directory, @NotNull final Logger logger) {
        this.directory = directory;
        this.crates = new File(this.directory, "crates");
        this.logger = logger;
    }

    /**
     * Loads the plugin.
     */
    @ApiStatus.Internal
    public void apply() {
        ConfigManager.load(this.directory);

        this.settings = new Settings();

        // Register default provider.
        CratesProvider.register(this);
    }

    @ApiStatus.Internal
    public void setUserManager(@NotNull final UserManager userManager) {
        if (this.userManager != null) return;

        this.userManager = userManager;
    }

    /**
     * Disables the plugin.
     */
    @ApiStatus.Internal
    public void disable() {
        // Unregister default provider.
        CratesProvider.unregister();
    }

    /**
     * Reloads the plugin.
     */
    @Override
    public void reload() {
        ConfigManager.refresh();
    }

    /**
     * @return the crates folder
     */
    @Override
    public @NotNull final File getCrateFolder() {
        return this.crates;
    }

    /**
     * @return the list of files in the crates folder
     */
    @Override
    public @NotNull final List<String> getCrateFiles() {
        return FileUtil.getFiles(getCrateFolder().toPath(), ".yml", true);
    }

    /**
     * @return the user manager
     */
    @Override
    public @NotNull final UserManager getUserManager() {
        return this.userManager;
    }

    /**
     * @return available config options
     */
    @Override
    public @NotNull final ISettings getSettings() {
        return this.settings;
    }

    /**
     * @return the plugin directory
     */
    @Override
    public @NotNull final File getDirectory() {
        return this.directory;
    }

    /**
     * @return the plugin logger
     */
    @Override
    public @NotNull final Logger getLogger() {
        return this.logger;
    }
}