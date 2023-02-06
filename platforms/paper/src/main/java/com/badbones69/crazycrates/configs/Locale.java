package com.badbones69.crazycrates.configs;

import com.badbones69.crazycrates.CrazyCrates;
import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;
import java.nio.file.Path;

@FileBuilder(isLogging = true, isAsync = false, isData = false, fileType = FileType.YAML)
public class Locale extends FileExtension {

    public Locale(Path path) {
        super(Config.LOCALE_FILE, path.resolve("locale"));
    }

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final Locale LOCALE = new Locale(plugin.getDirectory());

    public static void reload() {
        plugin.getPaperManager().getPaperFileManager().addFile(LOCALE);
    }
}