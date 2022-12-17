package com.badbones69.crazycrates.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    public static void extract(String input, Path output, boolean replace) {
        URL directory = FileUtil.class.getResource(input);

        if (directory == null) System.out.println("Could not find " + input + " in the jar!");

        assert directory != null;
        if (!directory.getProtocol().equals("jar")) System.out.println("Failed");

        ZipFile jar;
        try {
            System.out.println("Starting to extract locale from " + input + " directory in the jar...");

            jar = ((JarURLConnection) directory.openConnection()).getJarFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String filePath = input.substring(1);
        Enumeration<? extends ZipEntry> fileEntries = jar.entries();

        while (fileEntries.hasMoreElements()) {
            ZipEntry entry = fileEntries.nextElement();
            String entryName = entry.getName();

            if (!entryName.startsWith(filePath)) continue;

            Path outFile = output.resolve(entryName.substring(filePath.length()));
            boolean exists = Files.exists(outFile);

            if (!replace && exists) continue;

            if (entry.isDirectory()) {
                if (exists) {
                    System.out.println("File already exists.");
                    return;
                }

                try {
                    Files.createDirectories(outFile);

                    System.out.println("Directories have been created.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                continue;
            }

            try (InputStream in = jar.getInputStream(entry); OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile.toFile()))) {
                byte[] buffer = new byte[4096];

                int readCount;

                while ((readCount = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readCount);
                }

                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyFile(InputStream sourceFile, File destinationFile) {
        try (InputStream inputStream = sourceFile; FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int i;

            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
        } catch (Exception exception) {
            LoggerUtil.debug("<red>Failed to copy</red> <gold>" + destinationFile.getName() + "...</gold>", exception);
        }
    }
}