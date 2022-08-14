package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.badbones69.crazycrates.utilities.FileUtils;
import com.google.inject.Inject;

public class ConfigFile extends AbstractConfig {

    @Inject private FileUtils fileUtils;

    @Key("settings.language-file")
    @Comment("The language file to use from the locale folder.")
    public String LANGUAGE_FILE = "locale-en.yml";

    public void reload() {
        this.reload(fileUtils.PLUGIN_DIRECTORY.resolve("config.yml"), ConfigFile.class);
    }
}