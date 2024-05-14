package com.badbones69.crazycrates.config;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.enums.CustomFiles;
import com.badbones69.crazycrates.config.migrate.ConfigMigration;
import com.badbones69.crazycrates.config.migrate.LocaleMigration;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.badbones69.crazycrates.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.config.impl.messages.PlayerKeys;
import com.ryderbelserion.vital.common.configuration.YamlManager;
import org.jetbrains.annotations.ApiStatus;

public class ConfigManager {

    private static YamlManager yamlManager;

    /**
     * Loads configuration files.
     */
    @ApiStatus.Internal
    public static void load() {
        if (yamlManager == null) yamlManager = new YamlManager();

        // Create directory
        yamlManager.createPluginDirectory();

        // Add files
        yamlManager.addFile("messages.yml", new LocaleMigration(), MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class).addFile("config.yml", new ConfigMigration(), ConfigKeys.class)
                .addStaticFile("locations.yml")
                .addStaticFile("data.yml")
                .addFolder("crates")
                .addFolder("schematics")
                .init();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        // Refresh configme files.
        getYamlManager().reloadFiles();

        // Refresh custom files.
        getYamlManager().reloadCustomFiles();

        // Refresh other files.
        getYamlManager().reloadStaticFiles();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return CustomFiles.config.getSettingsManager();
    }

    /**
     * @return gets messages.yml
     */
    public static SettingsManager getMessages() {
        return CustomFiles.messages.getSettingsManager();
    }

    /**
     * @return yamlmanager object
     */
    public static YamlManager getYamlManager() {
        return yamlManager;
    }
}