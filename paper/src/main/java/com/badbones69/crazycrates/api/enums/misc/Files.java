package com.badbones69.crazycrates.api.enums.misc;

import com.badbones69.crazycrates.CrazyCrates;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;

public enum Files {

    respin_gui("respin-gui.yml", "guis"),

    crate_log("crates.log", "logs"),
    key_log("keys.log", "logs"),

    locations("locations.yml"),
    data("data.yml");


    private final String fileName;
    private final String folder;

    private final CrazyCrates plugin = CrazyCrates.getInstance();

    private final FileManager fileManager = this.plugin.getVital().getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName, final String folder) {
        this.fileName = fileName;
        this.folder = folder;
    }

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName) {
        this.fileName = fileName;
        this.folder = "";
    }

    public final YamlConfiguration getConfiguration() {
        return this.fileManager.getFile(this.fileName).getConfiguration();
    }

    public void reload() {
        this.fileManager.addFile(new File(this.plugin.getPlugin().getDataFolder(), this.fileName));
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }

    public final File getFile() {
        return new File(this.folder.isEmpty() ? this.plugin.getPlugin().getDataFolder() : new File(this.plugin.getPlugin().getDataFolder(), this.folder), this.fileName);
    }
}