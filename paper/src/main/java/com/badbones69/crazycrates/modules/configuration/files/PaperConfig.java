package com.badbones69.crazycrates.modules.configuration.files;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.nio.file.Path;

public class PaperConfig extends AbstractConfig {

    private static final PaperConfig CONFIG_FILE = new PaperConfig();

    public static void reload(Path path) {
        //CONFIG_FILE.handle(path.resolve("config.yml"), Config.class, crazyLogger);
    }
}