package com.badbones69.crazycrates.api.objects.other;

import com.ryderbelserion.vital.common.utils.FileUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import us.crazycrew.crazycrates.platform.IServer;
import com.badbones69.crazycrates.config.ConfigManager;
import java.io.File;
import java.util.List;

public class Server implements IServer {

    private final File directory;
    private final File crates;

    private UserManager userManager;
    private Options options;

    public Server(@NotNull final File directory) {
        this.directory = directory;
        this.crates = new File(this.directory, "crates");

        ConfigManager.load(this.directory);
    }

    /**
     * Loads the plugin.
     */
    @ApiStatus.Internal
    public void apply() {
        this.options = new Options();

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
     * @return the data folder
     */
    @Override
    public @NotNull File getDataFolder() {
        return this.directory;
    }

    /**
     * @return the list of files in the crates folder
     */
    @Override
    public @NotNull final List<String> getCrateFiles() {
        return FileUtil.getFiles(getCrateFolder(), ".yml");
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
        return this.options;
    }
}