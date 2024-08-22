package com.badbones69.crazycrates.api.enums;

import com.badbones69.crazycrates.CrazyCrates;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.jetbrains.annotations.NotNull;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public enum Files {

    locations("locations.yml"),
    data("data.yml");

    private final String fileName;

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final FileManager fileManager = this.plugin.getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName) {
        this.fileName = fileName;
    }

    public final YamlConfiguration getConfiguration() {
        return this.fileManager.getFile(this.fileName).getConfiguration();
    }

    public void reload() {
        this.fileManager.addFile(new File(this.plugin.getDataFolder(), this.fileName));
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }
}