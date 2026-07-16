package com.ryderbelserion.crazycrates.common.storage;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.storage.impl.file.SqliteFactory;
import com.ryderbelserion.crazycrates.common.storage.impl.objects.StorageHolder;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import org.jspecify.annotations.NullMarked;
import java.nio.file.Path;

@NullMarked
public final class StorageManager {

    private final CrazyCratesPlugin plugin;
    private final Path dataPath;

    public StorageManager(final CrazyCratesPlugin plugin) {
        this.plugin = plugin;

        this.dataPath = this.plugin.getDataPath();
    }

    public StorageHolder init() {
        final String type = "SQLITE".toLowerCase(); //todo() add config option support

        return switch (type) {
            case "sqlite" -> new StorageHolder(new SqliteFactory(this.dataPath.resolve("crazycrates.db")), this.plugin.getCrateRegistry()).init();

            default -> throw new FusionException("Unknown Database Type: %s".formatted(type));
        };
    }
}