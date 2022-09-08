package com.badbones69.crazycrates.modules.configuration.files.menus;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.nio.file.Path;

public class PaperMenuConfig extends AbstractConfig {

    private static final PaperMenuConfig CRATE_MENU_FILE = new PaperMenuConfig();

    public static void reload(Path path) {
        //CRATE_MENU_FILE.handle(path.resolve("crate-menu.yml"), PaperMenuConfig.class, crazyLogger);
    }
}