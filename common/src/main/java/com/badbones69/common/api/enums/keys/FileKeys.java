package com.badbones69.common.api.enums.keys;

import com.ryderbelserion.fusion.core.FusionCore;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.LogCustomFile;
import com.ryderbelserion.fusion.files.types.configurate.JsonCustomFile;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public enum FileKeys {

    // gui files
    crate_menu("crate-menu.yml", "guis", FileType.YAML),

    // static files
    messages("messages.yml", FileType.YAML),
    config("config.yml", FileType.YAML),

    // json files
    items("items.json", FileType.JSON);

    private final FusionCore fusion = FusionProvider.getInstance();
    private final FileManager fileManager = this.fusion.getFileManager();

    private final Path path = this.fusion.getDataPath();

    private final boolean isDirectory;
    private final FileType fileType;
    private final Path location;

    FileKeys(@NotNull final String fileName, @NotNull final String folder, @NotNull final FileType fileType) {
        this.location = this.path.resolve(folder).resolve(fileName);

        this.isDirectory = true;
        this.fileType = fileType;
    }

    FileKeys(@NotNull final String fileName, @NotNull final FileType fileType) {
        this.location = this.path.resolve(fileName);

        this.isDirectory = false;
        this.fileType = fileType;
    }

    public @NotNull final BasicConfigurationNode getJsonConfig() {
        return getJsonCustomFile().getConfiguration();
    }

    public JsonCustomFile getJsonCustomFile() {
        @NotNull final Optional<JsonCustomFile> customFile = this.fileManager.getJsonFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final CommentedConfigurationNode getYamlConfig() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getYamlCustomFile() {
        @NotNull final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final LogCustomFile getLogCustomFile() {
        @NotNull final Optional<LogCustomFile> customFile = this.fileManager.getLogFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public void addFile() {
        final Path parent = this.location.getParent();

        if (this.isDirectory && !Files.exists(parent)) {
            try {
                Files.createDirectory(parent);
            } catch (final IOException exception) {
                this.fusion.log(Level.ERROR, "Failed to create directory %s".formatted(parent));
            }
        }

        this.fileManager.addFile(this.location, this.fileType);
    }

    public void saveFile() {
        this.fileManager.saveFile(this.location);
    }

    public @NotNull final Path getPath() {
        return this.location;
    }
}