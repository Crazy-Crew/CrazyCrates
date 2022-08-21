package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.badbones69.crazycrates.utils.keys.Comment;
import com.badbones69.crazycrates.utils.keys.Key;
import java.nio.file.Path;

public class ConfigFile extends AbstractConfig {

    @Key("settings.language-file")
    @Comment("""
            The language file to use from the locale folder.
            Supported languages are Spanish(sp), Czech(cz) & English(en).""")
    public static String LANGUAGE_FILE = "locale-en.yml";

    private static final ConfigFile CONFIG_FILE = new ConfigFile();

    public static void reload(Path path, CrazyCrates plugin) {
        CONFIG_FILE.handle(path.resolve("config.yml"), ConfigFile.class, plugin);
    }
}