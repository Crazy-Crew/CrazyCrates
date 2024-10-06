package com.ryderbelserion.crazycrates.common.plugin.configs;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.ryderbelserion.crazycrates.common.plugin.configs.migration.ConfigMigration;
import com.ryderbelserion.crazycrates.common.plugin.configs.migration.LocaleMigration;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.config.ConfigKeys;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.locale.CommandKeys;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.locale.CrateKeys;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.locale.ErrorKeys;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.locale.MiscKeys;
import com.ryderbelserion.crazycrates.common.plugin.configs.types.locale.PlayerKeys;
import java.io.File;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager messages;

    /**
     * Loads configuration files.
     */
    public static void load(final File dataFolder) {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        config = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "config.yml"), builder)
                .migrationService(new ConfigMigration())
                .configurationData(ConfigKeys.class)
                .create();

        messages = SettingsManagerBuilder
                .withYamlFile(new File(dataFolder, "messages.yml"), builder)
                .migrationService(new LocaleMigration())
                .configurationData(MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .create();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        config.reload();
        messages.reload();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return config;
    }

    /**
     * @return gets messages.yml
     */
    public static SettingsManager getMessages() {
        return messages;
    }
}