package com.ryderbelserion.crazycrates.paper.api.managers;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.crazycrates.paper.api.objects.buttons.Button;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ButtonManager {

    private final FileManager fileManager;
    private final CratePlatform platform;

    private final FusionPaper fusion;

    public ButtonManager(@NotNull final CrazyCrates plugin) {
        this.platform = plugin.getPlatform();

        this.fileManager = this.platform.getFileManager();
        this.fusion = this.platform.getFusion();
    }

    private final Map<String, Button> buttons = new HashMap<>();

    public void load() {
        final Path folder = this.platform.getButtonsPath();

        this.buttons.clear();

        for (final Path path : this.fusion.getFilesByPath(folder, List.of(".yml"))) {
            final Optional<YamlCustomFile> optional = this.fileManager.getYamlFile(path);

            if (optional.isEmpty()) {
                continue;
            }

            final YamlCustomFile customFile = optional.get();

            final CommentedConfigurationNode configuration = customFile.getConfiguration();

            if (!configuration.hasChild("button")) {
                continue;
            }

            final String name = customFile.getPrettyName();

            this.buttons.put(name, new Button(configuration.node("button")));
        }
    }

    public @NotNull final Optional<Button> getButton(@NotNull final String name) {
        return Optional.ofNullable(this.buttons.get(name));
    }

    public @NotNull final Map<String, Button> getButtons() {
        return Collections.unmodifiableMap(this.buttons);
    }
}