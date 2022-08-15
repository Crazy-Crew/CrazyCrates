package com.badbones69.crazycrates.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Singleton
public class CommonUtils {

    @Inject private CrazyCrates plugin;

    public void copyFile(InputStream sourceFile, File destinationFile) {
        try (InputStream fis = sourceFile; FileOutputStream fos = new FileOutputStream(destinationFile)) {
            byte[] buf = new byte[1024];
            int i;

            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to copy " + destinationFile.getName() + "...");

            e.printStackTrace();
        }
    }
}