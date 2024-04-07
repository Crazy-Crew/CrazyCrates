package com.badbones69.crazycrates.api.utils;

import com.badbones69.crazycrates.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;

public class FileUtils {

    @NotNull
    private final static CrazyCrates plugin = CrazyCrates.get();

    public static void copyFiles(Path directory, String folder, List<String> names) {
        names.forEach(name -> copyFile(directory, folder, name));
    }

    public static void loadFiles() {
        File file = new File(plugin.getDataFolder(), "examples");

        if (file.exists()) {
            String[] entries = file.list();

            if (entries != null) {
                for (String entry : entries) {
                    File currentFile = new File(file.getPath(), entry);

                    currentFile.delete();
                }
            }

            file.delete();
        }

        copyFile(file.toPath(), "config.yml");
        copyFile(file.toPath(), "messages.yml");

        copyFiles(new File(file, "crates").toPath(), "crates", List.of(
                "CosmicCrateExample.yml",
                "CrateExample.yml",
                "QuadCrateExample.yml",
                "QuickCrateExample.yml",
                "WarCrateExample.yml",
                "CasinoExample.yml"
        ));
    }

    public static void copyFile(Path directory, String name) {
        File file = directory.resolve(name).toFile();

        if (file.exists()) return;

        File dir = directory.toFile();

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                if (MiscUtils.isLogging()) plugin.getLogger().warning("Created " + dir.getName() + " because we couldn't find it.");
            }
        }

        ClassLoader loader = plugin.getClass().getClassLoader();

        getResource(name, file, loader);
    }

    private static void getResource(String name, File file, ClassLoader loader) {
        URL resource = loader.getResource(name);

        if (resource == null) {
            if (MiscUtils.isLogging()) plugin.getLogger().severe("Failed to find file: " + name);

            return;
        }

        try {
            grab(resource.openStream(), file);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to copy file: " + name, exception);
        }
    }

    public static void copyFile(Path directory, String folder, String name) {
        File file = directory.resolve(name).toFile();

        if (file.exists()) return;

        File dir = directory.toFile();

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                if (MiscUtils.isLogging()) plugin.getLogger().warning("Created " + dir.getName() + " because we couldn't find it.");
            }
        }

        ClassLoader loader = plugin.getClass().getClassLoader();

        String url = folder + "/" + name;

        getResource(url, file, loader);
    }

    private static void grab(InputStream input, File output) throws Exception {
        try (InputStream inputStream = input; FileOutputStream outputStream = new FileOutputStream(output)) {
            byte[] buf = new byte[1024];
            int i;

            while ((i = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, i);
            }
        }
    }

    public static void cleanFiles() {
        FileConfiguration locations = Files.LOCATIONS.getFile();
        FileConfiguration data = Files.DATA.getFile();

        if (!locations.contains("Locations")) {
            locations.set("Locations.Clear", null);

            Files.LOCATIONS.saveFile();
        }

        if (!data.contains("Players")) {
            data.set("Players.Clear", null);

            Files.DATA.saveFile();
        }
    }
}