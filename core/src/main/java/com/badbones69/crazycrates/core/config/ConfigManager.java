package com.badbones69.crazycrates.core.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazycrates.core.config.impl.EditorKeys;
import com.badbones69.crazycrates.core.config.migrate.ConfigMigration;
import com.badbones69.crazycrates.core.config.migrate.LocaleMigration;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.core.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.core.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.core.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.core.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.core.config.impl.messages.PlayerKeys;
import java.nio.file.Path;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager editor;

    private static SettingsManager messages;

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

        messages = SettingsManagerBuilder
                .withYamlFile(path.resolve("messages.yml"), builder)
                .migrationService(new LocaleMigration())
                .configurationData(MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .create();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        config.reload();
        editor.reload();
        messages.reload();
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

    /**
     * @return gets messages.yml
     */
    public static SettingsManager getMessages() {
        return messages;
    }
}