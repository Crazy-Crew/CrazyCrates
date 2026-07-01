package com.badbones69.common.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.common.config.impl.EditorKeys;
import com.badbones69.common.config.migrate.ConfigMigration;
import com.badbones69.common.config.impl.ConfigKeys;
import java.nio.file.Path;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager editor;

    /**
     * Loads configuration files.
     */
    public static void load(final Path path) {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        config = SettingsManagerBuilder
                .withYamlFile(path.resolve("config.yml"), builder)
                .migrationService(new ConfigMigration())
                .configurationData(ConfigKeys.class)
                .create();

        editor = SettingsManagerBuilder
                .withYamlFile(path.resolve("editor.yml"), builder)
                .migrationService(new ConfigMigration())
                .configurationData(EditorKeys.class)
                .create();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        config.reload();
        editor.reload();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return config;
    }

    /**
     * @return gets editor.yml
     */
    public static SettingsManager getEditor() {
        return editor;
    }
}