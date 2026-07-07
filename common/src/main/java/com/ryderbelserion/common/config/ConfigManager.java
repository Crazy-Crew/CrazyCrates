package com.ryderbelserion.common.config;

import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import us.crazycrew.crazycrates.api.config.ConfigBuilder;
import us.crazycrew.crazycrates.api.config.impl.types.editor.EditorKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;

public final class ConfigManager {

    private PropertyManager config;

    public void init(@NonNull final Path path) {
        this.config = ConfigBuilder.withYamlPath(path)
                .configuration(EditorKeys.class)
                .withNodeStyle(NodeStyle.BLOCK)
                .withHeaderMode(HeaderMode.PRESERVE)
                .withIndent(1)
                .create();
    }

    public @NonNull PropertyManager getConfig() {
        return this.config;
    }
}