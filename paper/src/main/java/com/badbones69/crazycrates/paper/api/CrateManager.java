package com.badbones69.crazycrates.paper.api;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.*;

public class CrateManager {

    private final CratePlatform platform;
    private final FileManager fileManager;
    private final FusionPaper fusion;
    private final CrazyCrates plugin;

    private final Map<String, Crate> crates = new HashMap<>();

    public CrateManager(@NotNull final CratePlatform platform, @NotNull final CrazyCrates plugin) {
        this.platform = platform;

        this.fileManager = this.platform.getFileManager();
        this.fusion = this.platform.getFusion();

        this.plugin = plugin;
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

            final String fileName = strip(path, ".yml");

            final Crate crate = new Crate(child, fileName);

            this.crates.put(fileName, crate);
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