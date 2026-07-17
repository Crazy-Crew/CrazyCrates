package com.ryderbelserion.crazycrates.common.storage;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import com.ryderbelserion.crazycrates.common.storage.impl.file.types.YamlFactory;

public class StorageManager {

    private final CrazyCratesPlugin plugin;

    public StorageManager(final CrazyCratesPlugin plugin) {
        this.plugin = plugin;
    }

    public StorageHolder init() {
        return new StorageHolder(this.plugin, new YamlFactory()).init();
    }
}