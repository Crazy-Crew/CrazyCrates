package com.badbones69.crazycrates.core;

import com.badbones69.crazycrates.core.impl.Settings;
import com.ryderbelserion.fusion.core.FusionCore;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.ISettings;
import us.crazycrew.crazycrates.platform.IServer;
import com.badbones69.crazycrates.core.config.ConfigManager;
import java.nio.file.Path;
import java.util.List;

public class Server implements IServer {

    private final FusionCore fusion;
    private final Path crates;
    private final Path path;

    public Server(@NotNull final Path path, @NotNull final FusionCore fusion) {
        this.crates = path.resolve("crates");
        this.fusion = fusion;
        this.path = path;
    }

    private UserManager userManager;
    private Settings settings;

    public void apply() {
        ConfigManager.load(this.path);

        this.settings = new Settings();

        CratesProvider.register(this);
    }

    public void setUserManager(@NotNull final UserManager userManager) {
        if (this.userManager != null) return;

        this.userManager = userManager;
    }

    public void disable() {
        CratesProvider.unregister();
    }

    @Override
    public void reload() {
        ConfigManager.refresh();
    }

    @Override
    public @NotNull Path getCratesPath() {
        return this.crates;
    }

    @Override
    public @NotNull Path getDataPath() {
        return this.path;
    }

    @Override
    public @NotNull final List<String> getCrateFiles(final boolean removeExtension) {
        return this.fusion.getFileNames("crates", this.path, ".yml", removeExtension);
    }

    @Override
    public @NotNull final List<String> getCrateFiles() {
        return getCrateFiles(false);
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