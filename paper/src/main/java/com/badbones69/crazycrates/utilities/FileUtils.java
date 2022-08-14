package com.badbones69.crazycrates.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.inject.Inject;

import java.nio.file.Path;

public class FileUtils {

    @Inject private CrazyCrates plugin;

    public final Path DATA_DIRECTORY = plugin.getDataFolder().toPath().resolve("data");
    public final Path LOCALE_DIRECTORY = plugin.getDataFolder().toPath().resolve("locale");

    public final Path PLUGIN_DIRECTORY = plugin.getDataFolder().toPath();

}