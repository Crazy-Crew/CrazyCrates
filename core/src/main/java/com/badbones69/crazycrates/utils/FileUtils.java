package com.badbones69.crazycrates.utils;

import com.badbones69.crazycrates.utils.adventure.MsgWrapper;
import java.io.File;
import java.io.IOException;

public class FileUtils {

    public static void copyFile(File input, File output, org.simpleyaml.configuration.file.YamlFile configuration) {
        try {
            if (output.exists()) if (output.delete()) MsgWrapper.send("<#E0115F>Deleted <#11e092>" + output.getName() + " <#E0115F>because we have copied it.");
            
            configuration.save(input);
        } catch (IOException e) {
            MsgWrapper.send(e.getMessage());
        }
    }
}