package us.crazycrew.crazycrates.api.config.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import us.crazycrew.crazycrates.api.config.ConfigBuilder;
import us.crazycrew.crazycrates.api.config.impl.types.editor.EditorKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import us.crazycrew.crazycrates.api.enums.Files;

@ApiStatus.Internal
public final class ConfigManager {

    private PluginConfig pluginConfig;
    private EditorConfig editorConfig;

    public void init() {
        for (final Files file : Files.values()) {
            file.add();
        }

        this.pluginConfig = new PluginConfig();
        this.pluginConfig.init();

        this.editorConfig = new EditorConfig();
        this.editorConfig.init();
    }

    public void reload() {
        for (final Files file : Files.values()) {
            file.reload();
        }

        this.pluginConfig.init();
        this.editorConfig.init();
    }

    public @NonNull PluginConfig getPluginConfig() {
        return this.pluginConfig;
    }

    public @NonNull EditorConfig getEditorConfig() {
        return this.editorConfig;
    }
}