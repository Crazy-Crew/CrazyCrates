package com.ryderbelserion.crazycrates.common.storage;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import com.ryderbelserion.crazycrates.common.storage.impl.file.types.YamlFactory;
import com.ryderbelserion.crazycrates.common.storage.impl.sql.types.SqliteFactory;
import us.crazycrew.crazycrates.api.config.impl.ConfigManager;
import us.crazycrew.crazycrates.api.config.impl.types.config.DatabaseKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;

public class StorageManager {

    private final CrazyCratesPlugin plugin;
    private final ConfigManager configManager;

    public StorageManager(final CrazyCratesPlugin plugin) {
        this.configManager = plugin.getConfigManager();
        this.plugin = plugin;
    }

    public StorageHolder init() {
        final PropertyManager holder = this.configManager.getConfig();

        final String storageType = holder.getProperty(DatabaseKeys.storage_type);

        return switch (storageType.toLowerCase()) {
            case "yaml" -> new StorageHolder(new YamlFactory(this.plugin)).init();
            case "sqlite" -> new StorageHolder(new SqliteFactory(this.plugin)).init();
            default -> throw new IllegalStateException("Unexpected storage type value: " + storageType);
        };
    }
}