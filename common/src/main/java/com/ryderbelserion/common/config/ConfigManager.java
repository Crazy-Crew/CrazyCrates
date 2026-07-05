package com.ryderbelserion.common.config;

import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import us.crazycrew.crazycrates.api.config.ConfigBuilder;
import us.crazycrew.crazycrates.api.config.impl.types.editor.EditorKeys;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.List;

public final class ConfigManager {

    private PropertyManager config;

    public void init(@NonNull final Path path) {
        this.config = ConfigBuilder.withYamlPath(path)
                .configuration(EditorKeys.class)
                .withOptions(ConfigurationOptions.defaults().header(StringUtils.toString(List.of(
                        "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                        "Github: https://github.com/Crazy-Crew",
                        "",
                        "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                        "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                        "",
                        "Sounds: https://mudkipdev.github.io/minecraft-sound-explorer/"
                ))))
                .withNodeStyle(NodeStyle.BLOCK)
                .withHeaderMode(HeaderMode.PRESERVE)
                .withIndent(1)
                .create().setComment("test", "editor", "overwrite-old-crate-locations");
    }

    public @NonNull PropertyManager getConfig() {
        return this.config;
    }
}