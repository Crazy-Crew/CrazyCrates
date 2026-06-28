package com.badbones69.common;

import com.badbones69.common.config.ConfigManager;
import com.badbones69.common.impl.Settings;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NullMarked;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.KeyManager;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.nio.file.Path;
import java.util.List;

@NullMarked
public abstract class CrazyCratesPlugin extends CrazyCrates {

    private final FusionKyori fusion;

    public CrazyCratesPlugin(final FusionKyori fusion, final Path path) {
        super(path);

        this.fusion = fusion;
    }

    private UserManager userManager;
    private KeyManager keyManager;
    private Settings settings;

    @Override
    public void init() {
        this.fusion.init();

        ConfigManager.load(this.path);

        this.settings = new Settings();

        CratesProvider.register(this);
    }

    @Override
    public void reload() {
        ConfigManager.refresh();
    }

    @Override
    public void disable() {
        CratesProvider.unregister();
    }

    @Override
    public Path getCratesPath() {
        return this.path.resolve("crates");
    }

    @Override
    public Path getDataPath() {
        return this.path;
    }

    @Override
    public List<String> getCrateFiles(boolean removeExtension) {
        return this.fusion.getFilesByName("crates", this.path, ".yml", removeExtension);
    }

    @Override
    public UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public KeyManager getKeyManager() {
        return this.keyManager;
    }

    @Override
    public Settings getSettings() {
        return this.settings;
    }
}