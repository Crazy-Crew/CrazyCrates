package us.crazycrew.crazycrates.api.config.impl;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import us.crazycrew.crazycrates.api.config.ConfigBuilder;
import us.crazycrew.crazycrates.api.config.impl.types.config.RootKeys;
import us.crazycrew.crazycrates.api.config.impl.types.config.crate.CrateKeys;
import us.crazycrew.crazycrates.api.config.impl.types.config.gui.GuiKeys;
import us.crazycrew.crazycrates.api.config.impl.types.editor.EditorKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import us.crazycrew.crazycrates.api.enums.Files;

@ApiStatus.Internal
public final class ConfigManager {

    private PropertyManager editor;
    private PropertyManager config;

    public void init() {
        this.editor = ConfigBuilder.withYamlPath(Files.editor_config.getPath())
                .configuration(EditorKeys.class)
                .withNodeStyle(NodeStyle.BLOCK)
                .withHeaderMode(HeaderMode.PRESERVE)
                .withIndent(1)
                .create();

        this.config = ConfigBuilder.withYamlPath(Files.config.getPath())
                .configuration(RootKeys.class, GuiKeys.class, CrateKeys.class)
                .withNodeStyle(NodeStyle.BLOCK)
                .withHeaderMode(HeaderMode.PRESERVE)
                .withIndent(1)
                .create();
    }

    public void reload() {
        this.editor.reload();
        this.config.reload();
    }

    public @NonNull PropertyManager getEditorConfig() {
        return this.editor;
    }

    public @NonNull PropertyManager getConfig() {
        return this.config;
    }
}