package com.badbones69.crazycrates.modules.configuration.files;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.nio.file.Path;

public class PaperLocale extends AbstractConfig {

    private static final PaperLocale PAPER_LOCALE_FILE = new PaperLocale();

    public static void reload(Path path, String fileName) {
        //PAPER_LOCALE_FILE.handle(path.resolve(fileName), PaperLocale.class, crazyLogger);
    }
}