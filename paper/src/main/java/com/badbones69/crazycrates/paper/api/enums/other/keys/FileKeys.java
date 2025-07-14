package com.badbones69.crazycrates.paper.api.enums.other.keys;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.fusion.core.api.enums.FileAction;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.core.files.types.LogCustomFile;
import com.ryderbelserion.fusion.paper.files.FileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.nio.file.Path;
import java.util.ArrayList;

public enum FileKeys {

    respin_gui(FileType.PAPER, "respin-gui.yml", "guis"),

    crate_log(FileType.LOG, "crates.log", "logs"),
    key_log(FileType.LOG, "keys.log", "logs"),

    locations(FileType.PAPER, "locations.yml"),
    data(FileType.PAPER, "data.yml");

    private final CrazyCrates plugin = CrazyCrates.getPlugin();
    private final FileManager fileManager = this.plugin.getFileManager();
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
        @Nullable final PaperCustomFile customFile = this.fileManager.getPaperCustomFile(this.location);

        if (customFile == null) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile;
    }

    public @NotNull final LogCustomFile getLogCustomFile() {
        @Nullable final LogCustomFile customFile = (LogCustomFile) this.fileManager.getCustomFile(this.location);

        if (customFile == null) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile;
    }

    public @NotNull final FileType getFileType() {
        return this.fileType;
    }

    public @NotNull final Path getPath() {
        return this.location;
    }

    public void save(@NotNull final String content, @NotNull final ArrayList<FileAction> actions) {
        this.fileManager.saveFile(this.location, actions, content);
    }

    public void save() {
        this.fileManager.saveFile(this.location);
    }
}