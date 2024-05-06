package com.badbones69.crazycrates.platform.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import org.jetbrains.annotations.ApiStatus;
import com.badbones69.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.platform.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.platform.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.platform.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.platform.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.platform.config.impl.messages.PlayerKeys;
import com.badbones69.crazycrates.platform.config.migrate.ConfigMigration;
import com.badbones69.crazycrates.platform.config.migrate.LocaleMigration;
import java.io.File;

public class ConfigManager {

    private static SettingsManager config;

    private static SettingsManager messages;

    public static void load(File dataFolder) {
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

    public static void reload() {
        config.reload();

        messages.reload();
    }

    public static SettingsManager getConfig() {
        return config;
    }

    public static SettingsManager getMessages() {
        return messages;
    }
}