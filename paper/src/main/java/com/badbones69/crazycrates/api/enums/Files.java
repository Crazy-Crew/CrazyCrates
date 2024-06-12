package com.badbones69.crazycrates.api.enums;

import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public enum Files {

    locations("locations.yml"),
    data("data.yml");

    private final String fileName;
    private final String strippedName;
    private final YamlConfiguration configuration;

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName) {
        this.fileName = fileName;
        this.strippedName = this.fileName.replace(".yml", "");
        this.configuration = this.plugin.getFileManager().getFile(this.fileName);
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final String getStrippedName() {
        return this.strippedName;
    }

    public final YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public void save() {
        this.plugin.getFileManager().saveFile(this.fileName);
    }
}