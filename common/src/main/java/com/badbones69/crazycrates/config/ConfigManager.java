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
import org.jetbrains.annotations.ApiStatus;

public class ConfigManager {

    private static YamlManager yamlManager;

    /**
     * Loads configuration files.
     */
    @ApiStatus.Internal
    public static void load() {
        if (yamlManager == null) yamlManager = new YamlManager();

        yamlManager.addFile("messages.yml", new LocaleMigration(), MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .addFile("config.yml", new ConfigMigration(), ConfigKeys.class).addDefaultFile("crates", "CrateExample.yml")
                .addDefaultFile("crates", "AdvancedExample.yml")
                //.addDefaultFile("crates/types", "CosmicCrateExample.yml")
                //.addDefaultFile("crates/types", "QuickCrateExample.yml")
                //.addDefaultFile("crates/types", "QuadCrateExample.yml")
                //.addDefaultFile("crates/types", "WarCrateExample.yml")
                //.addDefaultFile("crates/types", "CasinoExample.yml")
                .addDefaultFile("schematics", "classic.nbt")
                .addDefaultFile("schematics", "nether.nbt")
                .addDefaultFile("schematics", "outdoors.nbt")
                .addDefaultFile("schematics", "sea.nbt")
                .addDefaultFile("schematics", "soul.nbt")
                .addDefaultFile("schematics", "wooden.nbt")
                //.addStaticFile("locations.yml")
                //.addStaticFile("data.yml")
                .addFolder("crates")
                .addFolder("schematics")
                .init();
    }

    /**
     * Refreshes configuration files.
     */
    public static void refresh() {
        // Reload static files
        getYamlManager().reloadFiles();
    }

    /**
     * @return gets config.yml
     */
    public static SettingsManager getConfig() {
        return getYamlManager().getFile("config.yml");
    }

    /**
     * @return gets messages.yml
     */
    public static SettingsManager getMessages() {
        return getYamlManager().getFile("messages.yml");
    }

    /**
     * @return yamlmanager object
     */
    public static YamlManager getYamlManager() {
        return yamlManager;
    }
}