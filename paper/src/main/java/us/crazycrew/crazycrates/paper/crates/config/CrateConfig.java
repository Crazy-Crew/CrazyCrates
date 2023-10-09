package us.crazycrew.crazycrates.paper.crates.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class CrateConfig extends YamlConfiguration {

    private final File file;

    public CrateConfig(File file) {
        this.file = file;
    }

    public void load() throws IOException, InvalidConfigurationException {
        load(this.file);
    }
}