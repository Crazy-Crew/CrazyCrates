package com.ryderbelserion.crazycrates.common;

import com.ryderbelserion.crazycrates.common.storage.StorageManager;
import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.enums.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class CrazyCratesPlugin<S> extends CrazyCrates<Component, S> {

    private final FusionKyori fusion;

    public CrazyCratesPlugin(final FusionKyori fusion, final Path path) {
        super(path);

        this.fusion = fusion;
    }

    protected StorageManager storageManager;
    protected StorageHolder storageHolder;
    protected ConfigManager configManager;

    public final StorageHolder getStorageHolder() {
        return this.storageHolder;
    }

    @Override
    public void init() {
        this.fusion.init();

        this.configManager = new ConfigManager();
        this.configManager.init();

        for (final Files key : Files.values()) {
            key.load();
        }

        this.storageManager = new StorageManager();

        this.storageHolder = this.storageManager.init();
        this.storageHolder.save();

        CratesProvider.register(this);
    }

    @Override
    public void reload() {
        this.fusion.reload();

        this.configManager.reload();

        for (final Files key : Files.values()) {
            key.reload();
        }

        this.storageHolder.save();

        loadMessages();
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

    @Override
    public @NonNull FusionKyori getFusion() {
        return this.fusion;
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