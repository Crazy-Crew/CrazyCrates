package com.badbones69.crazycrates.paper.api.enums.other.keys;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.fusion.core.exceptions.FusionException;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.LogCustomFile;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Optional;

public enum FileKeys {

    respin_gui(FileType.PAPER_YAML, "respin-gui.yml", "guis"),

    crate_log(FileType.LOG, "crates.log", "logs"),
    key_log(FileType.LOG, "keys.log", "logs"),

    locations(FileType.PAPER_YAML, "locations.yml"),
    data(FileType.PAPER_YAML, "data.yml");

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final PaperFileManager fileManager = this.plugin.getFileManager();
    private final Path path = this.plugin.getDataPath();

    private final FileType fileType;
    private final Path location; // the file location
    private final Path folder; // the folder which defaults to the data path

    FileKeys(@NotNull final FileType fileType, @NotNull final String fileName, @NotNull final String folder) {
        this.folder = this.path.resolve(folder);
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    FileKeys(@NotNull final FileType fileType, @NotNull final String fileName) {
        this.folder = this.path;
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    public @NotNull final YamlConfiguration getConfiguration() {
        return getPaperCustomFile().getConfiguration();
    }

    public @NotNull final PaperCustomFile getPaperCustomFile() {
        @NotNull final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.location);

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

    public @NotNull final FileType getFileType() {
        return this.fileType;
    }

    public @NotNull final Path getPath() {
        return this.location;
    }

    public void save() {
        this.fileManager.saveFile(this.location);
    }
}