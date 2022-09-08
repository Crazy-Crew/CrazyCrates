package com.badbones69.crazycrates.modules.configuration.files;

import com.badbones69.crazycrates.common.configuration.files.Config;
import com.badbones69.crazycrates.modules.configuration.PaperAbstractConfig;
import com.badbones69.crazycrates.api.utilities.logger.CrazyLogger;
import java.nio.file.Path;

public class PaperConfig extends PaperAbstractConfig {

    private static final PaperConfig CONFIG_FILE = new PaperConfig();

    public static void reload(Path path, CrazyLogger crazyLogger) {
        CONFIG_FILE.handle(path.resolve("config.yml"), Config.class, crazyLogger);
    }
}