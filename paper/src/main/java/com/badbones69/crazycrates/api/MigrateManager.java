package com.badbones69.crazycrates.api;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MigrateManager {

    @NotNull
    private static final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public static void migrate() {
        // Migrate config options
        migrateConfig();

        // Migrate old plugin config shit as we deleted it.
        migratePluginConfig();
    }

    private static void migrateConfig() {
        File input = new File(plugin.getDataFolder(), "config.yml");

        YamlConfiguration config = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        if (config.getString("Settings.Prefix") == null) return;

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager configKeys = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getDataFolder(), "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        String oldPrefix = config.getString("Settings.Prefix", "&8[&bCrazyCrates&8]: ");
        boolean oldMetrics = config.getBoolean("Settings.Toggle-Metrics");

        configKeys.setProperty(ConfigKeys.command_prefix, oldPrefix);
        configKeys.setProperty(ConfigKeys.toggle_metrics, oldMetrics);

        config.set("Settings.Prefix", null);
        config.set("Settings.Toggle-Metrics", null);

        try {
            configKeys.save();

            config.save(input);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save config.yml", exception);
        }
    }

    private static void migratePluginConfig() {
        File input = new File(plugin.getDataFolder(), "plugin-config.yml");

        if (!input.exists()) return;

        YamlConfiguration config = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager configKeys = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getDataFolder(), "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        boolean verbose = config.getBoolean("verbose_logging", false);
        boolean metrics = config.getBoolean("toggle_metrics", false);

        String prefix = config.getString("command_prefix", "&8[&bCrazyCrates&8]: ");
        String consolePrefix = config.getString("console_prefix", "&8[&bCrazyCrates&8] ");

        configKeys.setProperty(ConfigKeys.verbose_logging, verbose);
        configKeys.setProperty(ConfigKeys.toggle_metrics, metrics);
        configKeys.setProperty(ConfigKeys.command_prefix, prefix);
        configKeys.setProperty(ConfigKeys.console_prefix, consolePrefix);

        // Save to file.
        configKeys.save();

        // Delete old file.
        if (input.delete()) plugin.getLogger().warning("Successfully migrated " + input.getName());
    }
}