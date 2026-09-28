package com.ryderbelserion.crazycrates.common;

import com.ryderbelserion.crazycrates.common.storage.StorageManager;
import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.enums.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class CrazyCratesPlugin<S, W> extends CrazyCrates<Component, S> {

    private final FusionKyori fusion;

    public CrazyCratesPlugin(final FusionKyori fusion, final Path path) {
        super(path);

        this.fusion = fusion;
    }

    public @Nullable Key asWorld(final String world) {
        String value = world;

        switch (world) {
            case "world" -> value = "minecraft:overworld";
            case "world_the_end" -> value = "minecraft:the_end";
            case "world_nether" -> value = "minecraft:the_nether";
            default -> {
                if (!world.startsWith("minecraft:")) {
                    value = getWorld(world);
                }
            }
        }

        if (value.isBlank()) {
            return null;
        }

        return Key.key(value);
    }

    public abstract Optional<W> getWorld(final Key worldKey);

    public abstract String getWorld(final String worldName);

    public abstract boolean isCrateAvailable(final String name);

    protected StorageManager storageManager;
    protected StorageHolder storageHolder;
    protected ConfigManager configManager;

    public final StorageHolder getStorageHolder() {
        return this.storageHolder;
    }

    @Override
    public void init() {
        this.fusion.init().post();

        this.configManager = new ConfigManager();
        this.configManager.init();

        for (final Files key : Files.values()) {
            if (key.isExcluded()) continue;

            key.load();
        }

        this.storageManager = new StorageManager(this);

        this.storageHolder = this.storageManager.init();
        this.storageHolder.save();

        CratesProvider.register(this);
    }

    @Override
    public void reload() {
        this.fusion.reload();

        this.configManager.reload();

        for (final Files key : Files.values()) {
            if (key.isExcluded()) continue;

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