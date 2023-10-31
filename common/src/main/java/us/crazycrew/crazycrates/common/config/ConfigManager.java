package us.crazycrew.crazycrates.common.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import java.io.File;

public class ConfigManager {

    private final File dataFolder;

    public ConfigManager(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    private SettingsManager pluginConfig;

    private SettingsManager config;

    public void load() {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(new File(this.dataFolder, "plugin-config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(PluginConfig.class)
                .create();

        this.config = SettingsManagerBuilder
                .withYamlFile(new File(this.dataFolder, "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();
    }

    public void reload() {
        // Reload plugin-config.yml
        this.pluginConfig.reload();

        this.config.reload();
    }

    @NotNull
    public SettingsManager getPluginConfig() {
        return this.pluginConfig;
    }

    @NotNull
    public SettingsManager getConfig() {
        return config;
    }
}