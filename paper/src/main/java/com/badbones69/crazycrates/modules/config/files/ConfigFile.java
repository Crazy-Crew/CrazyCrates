package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.utils.ConfigurationUtils;
import com.badbones69.crazycrates.utils.keys.Comment;
import com.badbones69.crazycrates.utils.keys.Key;
import com.google.inject.Singleton;
import java.nio.file.Path;

@Singleton
public class ConfigFile extends ConfigurationUtils {

    @Key("settings.language-file")
    @Comment("The language file to use from the locale folder.")
    public String LANGUAGE_FILE = "locale-en.yml";

    public void reload(Path path, CrazyCrates plugin) {
        this.reload(path.resolve("config.yml"), ConfigFile.class, plugin);
    }
}