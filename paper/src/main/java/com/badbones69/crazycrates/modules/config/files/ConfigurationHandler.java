package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.inject.Inject;
import org.bukkit.configuration.file.FileConfiguration;
import java.io.File;
import java.util.HashMap;

public class ConfigurationHandler {

    @Inject private CrazyCrates plugin;

    private final HashMap<String, FileConfiguration> fileConfigurations = new HashMap<>();
    private final HashMap<String, File> currentFiles = new HashMap<>();

    public void loadFile(String fileName, File file, FileConfiguration fileConfiguration) {
        fileConfigurations.put(fileName, fileConfiguration);
        currentFiles.put(fileName, file);

        plugin.getLogger().warning("Loading " + fileName + "...");
    }

    public void reloadFile(String fileName, FileConfiguration fileConfiguration) {
        fileConfigurations.put(fileName, fileConfiguration);

        plugin.getLogger().warning("Reloading " + fileName + "...");
    }

    public void saveFile(String fileName) {
        try {
            fileConfigurations.get(fileName).save(getConfiguration(fileName));
        } catch (Exception ignored) {}
    }

    public void removeFile(String fileName) {
        fileConfigurations.remove(fileName);
        currentFiles.remove(fileName);
    }

    public FileConfiguration getFileConfiguration(String fileName) {
        return fileConfigurations.get(fileName);
    }

    public File getConfiguration(String fileName) {
        return currentFiles.get(fileName);
    }
}