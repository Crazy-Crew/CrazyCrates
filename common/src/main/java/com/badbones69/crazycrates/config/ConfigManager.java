package com.badbones69.crazycrates.config;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.config.migrate.ConfigMigration;
import com.badbones69.crazycrates.config.migrate.LocaleMigration;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.config.impl.messages.PlayerKeys;
import com.ryderbelserion.vital.common.configuration.YamlManager;

public class ConfigManager {

    private static YamlManager yamlManager;

    public static void load() {
        if (yamlManager == null) yamlManager = new YamlManager();

        yamlManager.addFile("messages.yml", new LocaleMigration(), MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .addFile("config.yml", new ConfigMigration(), ConfigKeys.class);
    }

    public static void reload() {
        yamlManager.reloadFiles();
    }

    public static SettingsManager getConfig() {
        return yamlManager.getFile("config.yml");
    }

    public static SettingsManager getMessages() {
        return yamlManager.getFile("messages.yml");
    }
}