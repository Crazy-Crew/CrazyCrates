package com.badbones69.crazycrates.api.enums;

import com.ryderbelserion.vital.files.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

public enum Files {

    /**
     * The locations.yml
     */
    locations("locations.yml"),
    /**
     * The data.yml
     */
    data("data.yml");

    private final String fileName;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the file configuration
     */
    public FileConfiguration getFile(FileManager fileManager) {
        return fileManager.getStaticFile(this.fileName);
    }

    /**
     * Saves a file from the enum.
     */
    public void save(FileManager fileManager) {
        fileManager.saveStaticFile(this.fileName);
    }

    /**
     * Reloads a file from the enum.
     */
    public void reload(FileManager fileManager) {
        fileManager.reloadStaticFile(this.fileName);
    }
}