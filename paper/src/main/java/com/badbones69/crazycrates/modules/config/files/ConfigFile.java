package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.google.inject.Singleton;
import java.nio.file.Path;

@Singleton
public class ConfigFile extends AbstractConfig {

    @Key("settings.language-file")
    @Comment("The language file to use from the locale folder.")
    public String LANGUAGE_FILE = "locale-en.yml";

    public void reload(Path path) {
        this.reload(path.resolve("config.yml"), ConfigFile.class);
    }
}