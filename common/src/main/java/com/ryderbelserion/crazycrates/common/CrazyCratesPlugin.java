package com.ryderbelserion.crazycrates.common;

import com.ryderbelserion.crazycrates.common.objects.crates.CrateLocation;
import com.ryderbelserion.crazycrates.common.registry.CrateRegistry;
import com.ryderbelserion.crazycrates.common.storage.StorageManager;
import com.ryderbelserion.crazycrates.common.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.enums.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class CrazyCratesPlugin<S, L> extends CrazyCrates<Component, S> {

    private final FusionKyori fusion;

    public CrazyCratesPlugin(final FusionKyori fusion, final Path path) {
        super(path);

        this.fusion = fusion;
    }

    public abstract CrateLocation map(@NonNull final L location);

    protected ConfigManager configManager;
    protected StorageHolder storageHolder;
    protected CrateRegistry crateRegistry;

    @Override
    public void init() {
        this.fusion.init();

        this.configManager = new ConfigManager();
        this.configManager.init();

        for (final Files key : Files.values()) {
            key.load();
        }

        this.crateRegistry = new CrateRegistry();

        try {
            this.storageHolder = new StorageManager(this).init();
        } catch (final Exception exception) {
            this.fusion.log(Level.ERROR, "Failed to initialize storage impl", exception);
        }

        CratesProvider.register(this);
    }

    @Override
    public void reload() {
        this.fusion.reload();

        this.configManager.reload();

        for (final Files key : Files.values()) {
            key.reload();
        }

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
    public @NonNull CrateRegistry getCrateRegistry() {
        return this.crateRegistry;
    }

    @Override
    public @NonNull ConfigManager getConfigManager() {
        return this.configManager;
    }

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

    public @NonNull StorageHolder getStorageHolder() {
        return this.storageHolder;
    }
}