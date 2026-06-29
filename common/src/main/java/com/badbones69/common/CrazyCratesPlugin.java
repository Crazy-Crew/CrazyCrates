package com.badbones69.common;

import com.badbones69.common.config.ConfigManager;
import com.badbones69.common.impl.Settings;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import java.nio.file.Path;
import java.util.List;

public abstract class CrazyCratesPlugin extends CrazyCrates {

    private final FusionKyori fusion;

    public CrazyCratesPlugin(final FusionKyori fusion, final Path path) {
        super(path);

        this.fusion = fusion;
    }

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

    public @NonNull FusionKyori getFusion() {
        return this.fusion;
    }

    @Override
    public @NonNull List<String> getCrateFiles(boolean removeExtension) {
        return this.fusion.getFilesByName("crates", this.path, ".yml", removeExtension);
    }

    @Override
    public @NonNull Path getCratesPath() {
        return this.path.resolve("crates");
    }

    @Override
    public @NonNull Settings getSettings() {
        return this.settings;
    }

    @Override
    public @NonNull Path getDataPath() {
        return this.path;
    }
}