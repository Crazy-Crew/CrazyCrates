package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.config.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class OldFileManager {
    
    private static final OldFileManager instance = new OldFileManager();
    private static final CrazyManager crazyManager = CrazyManager.getInstance();

    private final HashMap<Files, File> files = new HashMap<>();
    private final HashMap<Files, FileConfiguration> configurations = new HashMap<>();
    
    public static OldFileManager getInstance() {
        return instance;
    }

    private final Config config = new Config();

    /**
     * Sets up the plugin and loads all necessary files.
     */
    public void setup(CrazyCrates plugin) {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        files.clear();
        configurations.clear();

        // Loads all the normal static files.
        for (Files file : Files.values()) {
            File newFile = new File(plugin.getDataFolder(), file.getFileLocation());

            if (new Config().verbose) plugin.getLogger().info("Loading the " + file.getFileName());

            if (!newFile.exists()) {
                try {
                    File serverFile = new File(plugin.getDataFolder(), "/" + file.getFileLocation());
                    InputStream jarFile = getClass().getResourceAsStream("/" + file.getFileJar());
                    copyFile(jarFile, serverFile);
                } catch (Exception e) {
                    if (config.verbose) plugin.getLogger().warning("Failed to load file: " + file.getFileName());
                    e.printStackTrace();
                    continue;
                }
            }

            files.put(file, newFile);
            configurations.put(file, YamlConfiguration.loadConfiguration(newFile));

            if (config.verbose) plugin.getLogger().info("Successfully loaded " + file.getFileName());
        }

    }

    /**
     * Gets the file from the system.
     * @return The file from the system.
     */
    public FileConfiguration getFile(Files file) {
        return configurations.get(file);
    }

    /**
     * Saves the file from the loaded state to the file system.
     */
    public void saveFile(Files file) {
        try {
            configurations.get(file).save(files.get(file));
        } catch (IOException e) {
            crazyManager.getPlugin().getLogger().warning("Could not save " + file.getFileName() + "!");
            e.printStackTrace();
        }
    }
    
    /**
     * Overrides the loaded state file and loads the file systems file.
     */
    public void reloadFile(Files file) {
        configurations.put(file, YamlConfiguration.loadConfiguration(files.get(file)));
    }
    
    public void reloadAllFiles() {
        for (Files file : Files.values()) {
            file.reloadFile();
        }
    }
    
    /**
     * Was found here: https://bukkit.org/threads/extracting-file-from-jar.16962
     */
    private void copyFile(InputStream in, File out) throws Exception {
        try (InputStream fis = in; FileOutputStream fos = new FileOutputStream(out)) {
            byte[] buf = new byte[1024];
            int i;

            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        }
    }
    
    public enum Files {
        
        // ENUM_NAME("fileName.yml", "fileLocation.yml"),
        // ENUM_NAME("fileName.yml", "newFileLocation.yml", "oldFileLocation.yml"),
        CONFIG("config.yml", "config.yml"),
        MESSAGES("messages.yml", "messages.yml"),
        LOCATIONS("locations.yml", "locations.yml"),
        DATA("data.yml", "data.yml");
        
        private final String fileName;
        private final String fileJar;
        private final String fileLocation;
        
        /**
         * The files that the server will try and load.
         * @param fileName The file name that will be in the plugin's folder.
         * @param fileLocation The location the file in the plugin's folder.
         */
        Files(String fileName, String fileLocation) {
            this(fileName, fileLocation, fileLocation);
        }
        
        /**
         * The files that the server will try and load.
         * @param fileName The file name that will be in the plugin's folder.
         * @param fileLocation The location of the file will be in the plugin's folder.
         * @param fileJar The location of the file in the jar.
         */
        Files(String fileName, String fileLocation, String fileJar) {
            this.fileName = fileName;
            this.fileLocation = fileLocation;
            this.fileJar = fileJar;
        }
        
        /**
         * Get the name of the file.
         * @return The name of the file.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * The location the jar it is at.
         * @return The location in the jar the file is in.
         */
        public String getFileLocation() {
            return fileLocation;
        }
        
        /**
         * Get the location of the file in the jar.
         * @return The location of the file in the jar.
         */
        public String getFileJar() {
            return fileJar;
        }
        
        /**
         * Gets the file from the system.
         * @return The file from the system.
         */
        public FileConfiguration getFile() {
            return getInstance().getFile(this);
        }
        
        /**
         * Saves the file from the loaded state to the file system.
         */
        public void saveFile() {
            getInstance().saveFile(this);
        }
        
        /**
         * Overrides the loaded state file and loads the file systems file.
         */
        public void reloadFile() {
            getInstance().reloadFile(this);
        }
    }
}