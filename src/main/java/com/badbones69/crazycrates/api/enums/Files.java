package com.badbones69.crazycrates.api.enums;

import com.badbones69.crazycrates.CrazyCrates;
import com.ryderbelserion.vital.paper.files.config.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum Files {

    locations("locations.yml"),
    data("data.yml");

    private final String fileName;

    private @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

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
        return this.fileManager.getFile(this.fileName);
    }

    public void reload() {
        this.fileManager.reloadFile(this.fileName);
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }
}