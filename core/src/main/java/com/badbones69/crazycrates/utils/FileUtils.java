package com.badbones69.crazycrates.utils;

import com.badbones69.crazycrates.utils.adventure.MsgWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FileUtils {

    public static void copyFile(File input, File output, org.simpleyaml.configuration.file.YamlConfiguration configuration, Path directory) {
        try {
            if (output.exists()) {
                File updateDir = new File(directory + "/updates");

                String name = output.getName().replace(".yml", "").replace("v1", "");

                String fileName = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);

                File newOutput = new File(name + fileName + ".yml");

                if (output.renameTo(newOutput)) MsgWrapper.send("<#E0115F>Successfully added time stamp to <#11e092>" + output.getName() + ".");

                Files.move(newOutput.toPath(), Path.of(updateDir.toPath() + "/" + newOutput.getName()));

                MsgWrapper.send("<#E0115F>Successfully moved <#11e092>" + newOutput.getName() + " <#E0115F>to <#11e092>" + updateDir.getName());
            }

            configuration.save(input);
        } catch (IOException e) {
            MsgWrapper.send(e.getMessage());
        }
    }
}