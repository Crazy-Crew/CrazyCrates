package us.crazycrew.crazycrates.api.config.impl.types.editor;

import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.Files;

@ApiStatus.Internal
public final class EditorConfig {

    private CommentedConfigurationNode configuration;

    public void init() {
        this.configuration = Files.editor_config.getConfiguration();
    }

    public boolean isUpdatingOldLocations() {
        return this.configuration.node("editor", "overwrite-old-crate-locations").getBoolean(false);
    }
}