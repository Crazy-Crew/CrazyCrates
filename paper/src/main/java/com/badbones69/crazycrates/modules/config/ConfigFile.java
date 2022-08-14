package com.badbones69.crazycrates.modules.config;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.files.ConfigurationHandler;
import com.badbones69.crazycrates.modules.config.files.interfaces.IConfigFile;
import com.badbones69.crazycrates.utilities.Functions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.InputStream;

@Singleton
public class ConfigFile implements IConfigFile {

    @Inject
    @Named("ConfigFolder")
    private File configFolder;

    @Inject private ConfigurationHandler configurationHandler;

    @Inject private CrazyCrates plugin;

    @Inject private Functions functions;

    // The blank file we pass around.
    private File blankFile = null;

    private void create() {
        blankFile = new File(configFolder, "/" + "config.yml");

        if (blankFile.exists()) return;

        InputStream jarFile = plugin.getClass().getResourceAsStream("/" + "config.yml");

        functions.copyFile(jarFile, blankFile);
    }

    @Override
    public void load() {
        // Copy file if it does not exist.
        create();

        configurationHandler.loadFile(blankFile.getName(), blankFile, YamlConfiguration.loadConfiguration(blankFile));
    }

    @Override
    public void save() {
        // Copy file if it does not exist.
        create();

        configurationHandler.saveFile(blankFile.getName());
    }

    @Override
    public void reload() {
        configurationHandler.reloadFile(blankFile.getName(), YamlConfiguration.loadConfiguration(configurationHandler.getConfiguration(blankFile.getName())));
    }

    @Override
    public void remove() {
        configurationHandler.removeFile(blankFile.getName());
    }

    @Override
    public FileConfiguration getFile() {
        return configurationHandler.getFileConfiguration(blankFile.getName());
    }
}