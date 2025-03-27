package com.badbones69.crazycrates.paper.api.enums.other.keys;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.fusion.api.enums.FileType;
import com.ryderbelserion.fusion.api.exceptions.FusionException;
import com.ryderbelserion.fusion.paper.files.LegacyCustomFile;
import com.ryderbelserion.fusion.paper.files.LegacyFileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import java.io.File;

public enum FileKeys {

    respin_gui(FileType.YAML, "respin-gui.yml", "guis"),

    crate_log(FileType.NONE, "crates.log", "logs"),
    key_log(FileType.NONE, "keys.log", "logs"),

    locations(FileType.YAML, "locations.yml"),
    data(FileType.YAML, "data.yml");


    private final FileType fileType;
    private final String fileName;
    private final String folder;

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final LegacyFileManager fileManager = this.plugin.getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileType the file type
     * @param fileName the name of the file
     * @param folder the folder
     */
    FileKeys(final FileType fileType, final String fileName, final String folder) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.folder = folder;
    }

    /**
     * A constructor to build a file
     *
     * @param fileType the file type
     * @param fileName the name of the file
     */
    FileKeys(final FileType fileType, final String fileName) {
        this.fileType = fileType;
        this.fileName = fileName;
        this.folder = "";
    }

    public final YamlConfiguration getConfiguration() {
        @Nullable final LegacyCustomFile customFile = this.fileManager.getFile(this.fileName, this.fileType);

        if (customFile == null) {
            throw new FusionException("File configuration for " + this.fileName + " is null.");
        }

        return customFile.getConfiguration();
    }

    public FileType getFileType() {
        return this.fileType;
    }

    public void reload() {
        this.fileManager.addFile(this.fileName);
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }

    public final File getFile() {
        return new File(this.folder.isEmpty() ? this.plugin.getDataFolder() : new File(this.plugin.getDataFolder(), this.folder), this.fileName);
    }
}