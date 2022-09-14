package com.badbones69.crazycrates.modules.configuration.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.nio.file.Path;

public class PaperLocale extends AbstractConfig {

    private static final PaperLocale LOCALE_FILE = new PaperLocale();

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    public static void reload(Path path, String fileName) {
        LOCALE_FILE.handle(path.resolve(fileName), PaperLocale.class, plugin.getServer());
    }
}