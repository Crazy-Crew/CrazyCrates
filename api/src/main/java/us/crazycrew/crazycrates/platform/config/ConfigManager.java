package us.crazycrew.crazycrates.platform.config;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import us.crazycrew.crazycrates.CrazyCrates;
import us.crazycrew.crazycrates.CrazyCratesProvider;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.CommandKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.CrateKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.ErrorKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.MiscKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.PlayerKeys;
import java.io.File;

public class ConfigManager {

    private static final CrazyCrates instance = CrazyCratesProvider.get();

    private static SettingsManager config;

    private static SettingsManager messages;

    public static void load() {
        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        config = SettingsManagerBuilder
                .withYamlFile(new File(instance.getServer().getFolder(), "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        messages = SettingsManagerBuilder
                .withYamlFile(new File(instance.getServer().getFolder(), "messages.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .create();
    }

    public static void reload() {
        config.reload();

        messages.reload();
    }

    public static void save() {
        config.save();

        messages.save();
    }

    public static SettingsManager getConfig() {
        //if (config == null) {
        //    instance.getLogger().warning("Unsupported Action: Trying to fetch config.yml while the variable is null.");
        //}

        return config;
    }

    public static SettingsManager getMessages() {
        //if (messages == null) {
        //    instance.getLogger().warning("Unsupported Action: Trying to fetch messages.yml while the variable is null.");
        //}

        return messages;
    }
}