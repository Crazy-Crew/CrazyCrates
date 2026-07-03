package com.badbones69.common;

import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.config.ConfigManager;
import us.crazycrew.crazycrates.platform.ISettings;
import java.nio.file.Path;
import java.util.List;

public abstract class CrazyCratesPlugin<S> extends CrazyCrates<Component, S> {

    private final FusionKyori fusion;

    public CrazyCratesPlugin(final FusionKyori fusion, final Path path) {
        super(path);

        this.fusion = fusion;
    }

    protected ConfigManager configManager;

    @Override
    public void init() {
        this.fusion.init();

        this.configManager = new ConfigManager();
        this.configManager.init();

        CratesProvider.register(this);
    }

    @Override
    public void reload() {

    }

    @Override
    public void disable() {
        CratesProvider.unregister();
    }

    @Override
    public @NonNull List<String> getCrateFiles(boolean removeExtension) {
        return this.fusion.getFilesByName("crates", this.path, ".yml", removeExtension);
    }

    @Override
    public @NonNull ConfigManager getConfigManager() {
        return this.configManager;
    }

    public @NonNull FusionKyori getFusion() {
        return this.fusion;
    }

    @Override
    public @NonNull ISettings getSettings() {
        return this.configManager.getPluginConfig();
    }

    @Override
    public @NonNull Path getCratesPath() {
        return this.path.resolve("crates");
    }

    @Override
    public @NonNull Path getDataPath() {
        return this.path;
    }
}