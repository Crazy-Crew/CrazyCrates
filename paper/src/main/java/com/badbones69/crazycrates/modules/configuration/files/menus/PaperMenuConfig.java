package com.badbones69.crazycrates.modules.configuration.files.menus;

import com.badbones69.crazycrates.modules.configuration.PaperAbstractConfig;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import java.nio.file.Path;

public class PaperMenuConfig extends PaperAbstractConfig {

    private static final PaperMenuConfig CRATE_MENU_FILE = new PaperMenuConfig();

    public static void reload(Path path, CrazyLogger crazyLogger) {
        CRATE_MENU_FILE.handle(path.resolve("crate-menu.yml"), PaperMenuConfig.class, crazyLogger);
    }
}