package com.ryderbelserion.crazycrates.paper.api;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.objects.crate.Crate;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.*;

public class CrateManager {

    private final FileManager fileManager;
    private final CratePlatform platform;

    private final CrazyCrates plugin;

    private final FusionPaper fusion;

    private final Map<String, Crate> crates = new HashMap<>();

    public CrateManager(@NotNull final CrazyCrates plugin) {
        this.plugin = plugin;

        this.platform = this.plugin.getPlatform();

        this.fileManager = this.platform.getFileManager();
        this.fusion = this.platform.getFusion();
    }

    public void load() {
        final Path folder = this.platform.getCratesPath();

        this.crates.clear();

        for (final Path path : this.fusion.getFilesByPath(folder, List.of(".yml"))) {
            final Optional<YamlCustomFile> optional = this.fileManager.getYamlFile(path);

            if (optional.isEmpty()) {
                continue;
            }

            final CommentedConfigurationNode configuration = optional.get().getConfiguration();

            if (!configuration.hasChild("crate")) {
                continue;
            }

            final CommentedConfigurationNode child = configuration.node("crate");

            final String cleanName = strip(path, ".yml");

            final Crate crate = new Crate(child, cleanName);

            this.crates.put(cleanName, crate);
        }
    }

    public String strip(@NotNull final Path path, @NotNull final String extension) {
        return path.getFileName().toString().replace(extension, "");
    }

    public @NotNull final Optional<Crate> getCrate(@NotNull final String name) {
        return Optional.ofNullable(this.crates.get(name));
    }

    public @NotNull final Map<String, Crate> getCrates() {
        return Collections.unmodifiableMap(this.crates);
    }
}