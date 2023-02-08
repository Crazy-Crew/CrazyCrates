package com.badbones69.crazycrates.utils;

import com.badbones69.crazycrates.CrazyCrates;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    public static void copyFile(File input, File output, org.simpleyaml.configuration.file.YamlConfiguration configuration, CrazyCrates plugin) {
        try {
            if (output.exists()) {
                File updateDir = new File(plugin.getDirectory() + "/updates");

                String name = output.getName().replace(".yml", "").replace("v1", "");

                String fileName = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

                File newOutput = new File(name + fileName + ".yml");

                if (output.renameTo(newOutput)) plugin.getLogger().warning("Successfully added time stamp to " + output.getName() + ".");

                Files.move(newOutput.toPath(), Path.of(updateDir.toPath() + "/" + newOutput.getName()));

                plugin.getLogger().warning("Successfully moved " + newOutput.getName() + " to " + updateDir.getName());
            }

            configuration.save(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}