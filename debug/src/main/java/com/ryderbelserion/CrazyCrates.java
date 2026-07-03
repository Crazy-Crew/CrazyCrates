package com.ryderbelserion;

import com.ryderbelserion.api.api.properties.PropertyManager;
import com.ryderbelserion.config.ConfigManager;
import com.ryderbelserion.config.types.ConfigKeys;
import com.ryderbelserion.config.types.TestKeys;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

public class CrazyCrates extends JavaPlugin {

    private FusionPaper fusion;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);
        this.fusion.init();

        final Path path = getDataPath();
        final Path config = path.resolve("config.yml");

        final ConfigManager configManager = new ConfigManager();

        configManager.init(config);

        final PropertyManager settings = configManager.getConfig();

        this.fusion.log(Level.WARNING, "<red>File Type: %s, <yellow>Path: %s", settings.getFileType(), settings.getPath());

        this.fusion.log(Level.WARNING, "<green>String Value: %s", settings.getProperty(ConfigKeys.example_property));

        this.fusion.log(Level.WARNING, "<blue>Integer Value: %s", settings.getProperty(ConfigKeys.example_integer_property));

        this.fusion.log(Level.WARNING, "<yellow>Boolean Value: %s", settings.getProperty(ConfigKeys.example_boolean_property));

        settings.getProperty(ConfigKeys.example_list_property).forEach(line -> {
            this.fusion.log(Level.WARNING, "<light_purple>List Value: %s", line);
        });

        settings.setProperty(ConfigKeys.example_property, "this is a new new property!");

        this.fusion.log(Level.WARNING, "<gold>New Property: %s", settings.getProperty(ConfigKeys.example_property));

        this.fusion.log(Level.WARNING, "<gold>Test Property: %s", settings.getProperty(TestKeys.example_property));
    }
}