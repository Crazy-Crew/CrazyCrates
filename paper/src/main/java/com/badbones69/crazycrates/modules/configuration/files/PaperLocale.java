package com.badbones69.crazycrates.modules.configuration.files;

import com.badbones69.crazycrates.modules.configuration.PaperAbstractConfig;
import com.badbones69.crazycrates.api.utilities.logger.CrazyLogger;
import java.nio.file.Path;

public class PaperLocale extends PaperAbstractConfig {

    private static final PaperLocale PAPER_LOCALE_FILE = new PaperLocale();

    public static void reload(Path path, String fileName, CrazyLogger crazyLogger) {
        PAPER_LOCALE_FILE.handle(path.resolve(fileName), PaperLocale.class, crazyLogger);
    }
}