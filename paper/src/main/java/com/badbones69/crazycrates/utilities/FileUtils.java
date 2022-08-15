package com.badbones69.crazycrates.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.files.ConfigFile;
import com.badbones69.crazycrates.modules.config.files.LocaleFile;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// Billy is a cool human.
@Singleton
public class FileUtils {

    @Inject private CrazyLogger crazyLogger;

    public void create(ConfigFile configFile, LocaleFile localeFile, Path localeDirectory, Path pluginDirectory, CrazyCrates plugin) {
        extract("/locale/", localeDirectory, false, plugin);

        configFile.reload(pluginDirectory, plugin);
        localeFile.reload(pluginDirectory, configFile.LANGUAGE_FILE, plugin);
    }

    public void extract(String inDir, Path outDir, boolean replace, CrazyCrates plugin) {
        // https://coderanch.com/t/472574/java/extract-directory-current-jar

        URL dirURL = plugin.getClass().getResource(inDir);

        if (dirURL == null) throw new IllegalStateException("can't find " + inDir + " on the classpath");

        if (!dirURL.getProtocol().equals("jar")) throw new IllegalStateException("don't know how to handle extracting from " + dirURL);

        ZipFile jar;

        try {
            crazyLogger.debug("Extracting " + inDir + " directory from the jar...");
            jar = ((JarURLConnection) dirURL.openConnection()).getJarFile();
        } catch (IOException e) {
            crazyLogger.debug("<red>Failed to extract directory from jar.</red>");
            throw new RuntimeException(e);
        }

        String path = inDir.substring(1);
        Enumeration<? extends ZipEntry> entries = jar.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();

            if (!name.startsWith(path)) continue;

            Path file = outDir.resolve(name.substring(path.length()));
            boolean exists = Files.exists(file);

            if (!replace && exists) {
                crazyLogger.debug("<gold>" + name + " exists already.</gold>");
                continue;
            }

            if (entry.isDirectory()) {
                if (!exists) {
                    try {
                        Files.createDirectories(file);
                        crazyLogger.debug("<green> Creating</green>" + name);
                    } catch (IOException e) {
                        crazyLogger.debug("<red><bold>Failed</bold></red> to create " + name);
                    }
                } else {
                    crazyLogger.debug(name + " <yellow>already exists</yellow>");
                }

                continue;
            }

            try (InputStream in = jar.getInputStream(entry); OutputStream out = new BufferedOutputStream(new FileOutputStream(file.toFile()))) {
                byte[] buffer = new byte[4096];
                int readCount;

                while ((readCount = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readCount);
                }

                out.flush();
                //Logger.debug("  <green>writing</green>  " + name);
            } catch (IOException e) {
               // Logger.debug("  <red><bold>failed</bold></red>   " + name);
                //Logger.warn("Failed to extract file (" + name + ") from jar!");
                e.printStackTrace();
            }
        }
    }
}