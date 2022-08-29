package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyLogger crazyLogger;

    public FileManager(CrazyLogger crazyLogger) {
        this.crazyLogger = crazyLogger;
    }

    private boolean isLogging = false;

    private final ArrayList<String> homeFolders = new ArrayList<>();
    private final ArrayList<CustomFile> customFiles = new ArrayList<>();
    private final HashMap<String, String> jarHomeFolders = new HashMap<>();
    private final HashMap<String, String> autoGenerateFiles = new HashMap<>();
    
    /**
     * Sets up the plugin and loads all necessary files.
     */
    public void setup() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();

        customFiles.clear();

        // Starts to load all the custom files.
        if (homeFolders.size() > 0) {
            if (isLogging) crazyLogger.debug("<red>Loading custom files.</red>");

            for (String homeFolder : homeFolders) {
                File homeFile = new File(plugin.getDataFolder(), "/" + homeFolder);

                if (homeFile.exists()) {
                    String[] list = homeFile.list();

                    if (list != null) {
                        for (String name : list) {
                            if (name.endsWith(".yml")) {
                                CustomFile file = new CustomFile(name, homeFolder, plugin);

                                if (file.exists()) {
                                    customFiles.add(file);

                                    if (isLogging) crazyLogger.debug("<red>Loaded new custom file:</red> <gold>" + homeFolder + "/" + name + ".</gold>");
                                }
                            }
                        }
                    }
                } else {
                    homeFile.mkdir();

                    if (isLogging) crazyLogger.debug("<red>The folder</red> <gold>" + homeFolder + "/</gold> <red>was not found so it was created.</red>");

                    for (String fileName : autoGenerateFiles.keySet()) {
                        if (autoGenerateFiles.get(fileName).equalsIgnoreCase(homeFolder)) {
                            homeFolder = autoGenerateFiles.get(fileName);

                            try {
                                File serverFile = new File(plugin.getDataFolder(), homeFolder + "/" + fileName);
                                InputStream jarFile = getClass().getResourceAsStream((jarHomeFolders.getOrDefault(fileName, homeFolder)) + "/" + fileName);
                                copyFile(jarFile, serverFile);

                                if (fileName.toLowerCase().endsWith(".yml")) customFiles.add(new CustomFile(fileName, homeFolder, plugin));

                                if (isLogging) crazyLogger.debug("<red>Created new default file:</red> <gold>" + homeFolder + "/" + fileName + ".</gold>");
                            } catch (Exception e) {
                                if (isLogging) {
                                    crazyLogger.debug("<red>Failed to create new default file:</red> <gold>" + homeFolder + "/" + fileName + "!</gold>");
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            if (isLogging) crazyLogger.debug("<red>Finished loading custom files.</red>");
        }
    }

    public void copyFile(InputStream sourceFile, File destinationFile) {
        try (InputStream fis = sourceFile; FileOutputStream fos = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int i;

            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            crazyLogger.debug("<red>Failed to copy</red> <gold>" + destinationFile.getName() + "...</gold>");

            e.printStackTrace();
        }
    }
    
    /**
     * Turn on the logger system for the FileManager.
     * @param isLogging True to turn it on and false for it to be off.
     */
    public FileManager toggleLogging(boolean isLogging) {
        this.isLogging = isLogging;
        return this;
    }
    
    /**
     * Check if the logger is logging in console.
     * @return True if it is and false if it isn't.
     */
    public boolean isLogging() {
        return this.isLogging;
    }
    
    /**
     * Register a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
     * @param homeFolder The folder that has custom files in it.
     */
    public FileManager registerCustomFilesFolder(String homeFolder) {
        homeFolders.add(homeFolder);
        return this;
    }
    
    /**
     * Unregister a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
     * @param homeFolder The folder with custom files in it.
     */
    public FileManager unregisterCustomFilesFolder(String homeFolder) {
        homeFolders.remove(homeFolder);
        return this;
    }
    
    /**
     * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
     * @param homeFolder The folder that has custom files in it.
     */
    public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        return this;
    }
    
    /**
     * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
     * @param homeFolder The folder that has custom files in it.
     * @param jarHomeFolder The folder that the file is found in the jar.
     */
    public FileManager registerDefaultGenerateFiles(String fileName, String homeFolder, String jarHomeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        jarHomeFolders.put(fileName, jarHomeFolder);
        return this;
    }
    
    /**
     * Unregister a file that doesn't need to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The file that you want to remove from auto-generating.
     */
    public FileManager unregisterDefaultGenerateFiles(String fileName) {
        autoGenerateFiles.remove(fileName);
        jarHomeFolders.remove(fileName);
        return this;
    }
    
    /**
     * Get a custom file from the loaded custom files instead of a hardcoded one.
     * This allows you to get custom files like Per player data files.
     * @param name Name of the crate you want. (Without the .yml)
     * @return The custom file you wanted otherwise if not found will return null.
     */
    public CustomFile getFile(String name) {
        for (CustomFile file : customFiles) {
            if (file.getName().equalsIgnoreCase(name)) return file;
        }

        return null;
    }
    
    /**
     * Save a custom file.
     * @param name The name of the custom file.
     */
    public void saveFile(String name) {
        CustomFile file = getFile(name);

        if (file != null) {
            try {
                file.getFile().save(new File(plugin.getDataFolder(), file.getHomeFolder() + "/" + file.getFileName()));

                if (isLogging) crazyLogger.debug("<red>Successfully saved the</red> <gold>" + file.getFileName() + ".</gold>");
            } catch (Exception e) {
                if (isLogging) {
                    crazyLogger.debug("<red>Could not save</red> <gold>" + file.getFileName() + "!</gold>");
                    e.printStackTrace();
                }
            }
        } else {
            if (isLogging) crazyLogger.debug("<red>The file</red> <gold>" + name + ".yml</gold> <red>could not be found!</red>");
        }
    }
    
    /**
     * Save a custom file.
     * @param file The custom file you are saving.
     * @return True if the file saved correct and false if there was an error.
     */
    public Boolean saveFile(CustomFile file) {
        return file.saveFile();
    }
    
    /**
     * Overrides the loaded state file and loads the file systems file.
     */
    public void reloadFile(String name) {
        CustomFile file = getFile(name);

        if (file != null) {
            try {
                file.file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + file.getHomeFolder() + "/" + file.getFileName()));

                if (isLogging) crazyLogger.debug("<red>Successfully reloaded the</red> <gold>" + file.getFileName() + ".</gold>");
            } catch (Exception e) {
                if (isLogging) {
                    crazyLogger.debug(("<red>Could not reload the</red> <gold>" + file.getFileName() + "!</gold>"));
                    e.printStackTrace();
                }
            }
        } else {
            if (isLogging) crazyLogger.debug("<red>The file</red> <gold>" + name + ".yml</gold> <red>could not be found!</red>");
        }
    }
    
    /**
     * Overrides the loaded state file and loads the filesystems file.
     * @return True if it reloaded correct and false if the file wasn't found.
     */
    public Boolean reloadFile(CustomFile file) {
        return file.reloadFile();
    }
    
    public void reloadAllFiles() {
        customFiles.forEach(CustomFile::reloadFile);
    }
    
    public ArrayList<String> getAllCratesNames() {
        ArrayList<String> files = new ArrayList<>();

        String[] cratesFolder = new File(plugin.getDataFolder(), "/crates").list();

        assert cratesFolder != null;
        for (String name : cratesFolder) {
            if (!name.endsWith(".yml")) continue;

            files.add(name.replaceAll(".yml", ""));
        }

        return files;
    }
    
    public class CustomFile {
        
        private final String name;
        private final String fileName;
        private final String homeFolder;
        private FileConfiguration file;

        private final CrazyCrates plugin;
        
        /**
         * A custom file that is being made.
         * @param name Name of the file.
         * @param homeFolder The home folder of the file.
         */
        public CustomFile(String name, String homeFolder, CrazyCrates plugin) {
            this.name = name.replace(".yml", "");
            this.plugin = plugin;
            this.fileName = name;
            this.homeFolder = homeFolder;

            if (new File(plugin.getDataFolder(), "/" + homeFolder).exists()) {
                if (new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name).exists()) {
                    file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + name));
                } else {
                    file = null;
                }
            } else {
                new File(plugin.getDataFolder(), "/" + homeFolder).mkdir();

                if (isLogging) crazyLogger.debug("<red>The folder</red> <gold>" + homeFolder + "/</gold> <red>was not found so it was created.</red>");

                file = null;
            }
        }
        
        /**
         * Get the name of the file without the .yml part.
         * @return The name of the file without the .yml.
         */
        public String getName() {
            return name;
        }
        
        /**
         * Get the full name of the file.
         * @return Full name of the file.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * Get the name of the home folder of the file.
         * @return The name of the home folder the files are in.
         */
        public String getHomeFolder() {
            return homeFolder;
        }
        
        /**
         * Get the ConfigurationFile.
         * @return The ConfigurationFile of this file.
         */
        public FileConfiguration getFile() {
            return file;
        }
        
        /**
         * Check if the file actually exists in the file system.
         * @return True if it does and false if it doesn't.
         */
        public Boolean exists() {
            return file != null;
        }
        
        /**
         * Save the custom file.
         * @return True if it saved correct and false if something went wrong.
         */
        public Boolean saveFile() {
            if (file != null) {
                try {
                    file.save(new File(plugin.getDataFolder(), homeFolder + "/" + fileName));

                    if (isLogging) crazyLogger.debug("<red>Successfully saved the</red> <gold>" + fileName + ".</gold>");

                    return true;
                } catch (Exception e) {
                    if (isLogging) {
                        crazyLogger.debug(("<red>Could not save</red> <gold>" + fileName + "!</gold>"));
                        e.printStackTrace();
                    }

                    return false;
                }
            } else {
                if (isLogging) crazyLogger.debug("<red>There was a null custom file that could not be found!</red>");
            }

            return false;
        }
        
        /**
         * Overrides the loaded state file and loads the filesystems file.
         * @return True if it reloaded correct and false if the file wasn't found or error.
         */
        public Boolean reloadFile() {
            if (file != null) {
                try {
                    file = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "/" + homeFolder + "/" + fileName));

                    if (isLogging) crazyLogger.debug("<red>Successfully reloaded the</red> <gold>" + fileName + ".</gold>");

                    return true;
                } catch (Exception e) {
                    if (isLogging) {
                        crazyLogger.debug(("<red>Could not reload the</red> <gold>\" + fileName + \"!</gold>"));
                        e.printStackTrace();
                    }
                }
            } else {
                if (isLogging) plugin.getLogger().warning("<red>There was a null custom file that was not found!</red>");
            }

            return false;
        }
    }
}