package com.ryderbelserion.crazycrates.paper.api;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.objects.crate.Crate;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NullMarked
public final class CrateManager {

    private final FileManager fileManager;
    private final CratePlatform platform;

    private final FusionPaper fusion;

    public CrateManager(final CrazyCrates plugin) {
        this.platform = plugin.getPlatform();

        this.fileManager = this.platform.getFileManager();
        this.fusion = this.platform.getFusion();
    }

    private final Map<String, Crate> crates = new ConcurrentHashMap<>();

    public void load() {
        final Path folder = this.platform.getCratesPath();

        this.crates.clear();

        for (final Path path : this.fusion.getFilesByPath(folder, List.of(".yml"))) {
            final Optional<YamlCustomFile> optional = this.fileManager.getYamlFile(path);

            if (optional.isEmpty()) {
                continue;
            }

            final YamlCustomFile customFile = optional.get();

            final CommentedConfigurationNode configuration = customFile.getConfiguration();

            if (!configuration.hasChild("crate")) {
                continue;
            }

            final CommentedConfigurationNode child = configuration.node("crate");

            final String name = customFile.getPrettyName();

            final Crate crate = new Crate(child, name);

            this.crates.put(name, crate);
        }
    }

    public Optional<Crate> getCrate(final String name) {
        return Optional.ofNullable(this.crates.get(name));
    }

    public Map<String, Crate> getCrates() {
        return Collections.unmodifiableMap(this.crates);
    }
}