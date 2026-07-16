package com.ryderbelserion.crazycrates.common.storage;

import com.ryderbelserion.crazycrates.common.storage.holder.StorageHolder;
import com.ryderbelserion.crazycrates.common.storage.impl.file.types.YamlFactory;

public class StorageManager {

    public StorageHolder init() {
        return new StorageHolder(new YamlFactory()).init();
    }
}