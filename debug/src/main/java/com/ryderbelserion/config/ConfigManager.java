package com.ryderbelserion.config;

import com.ryderbelserion.api.api.ConfigBuilder;
import com.ryderbelserion.api.api.properties.PropertyManager;
import com.ryderbelserion.config.types.ConfigKeys;
import com.ryderbelserion.config.types.SecondaryKeys;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

public final class ConfigManager {

    private PropertyManager config;

    public void init(@NonNull final Path path) {
        this.config = ConfigBuilder.withYamlPath(path).configuration(ConfigKeys.class, SecondaryKeys.class).create();
    }

    public @NonNull PropertyManager getConfig() {
        return this.config;
    }
}