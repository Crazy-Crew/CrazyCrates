package com.badbones69.crazycrates.configs.convert;

import com.badbones69.crazycrates.configs.Config;
import com.badbones69.crazycrates.utils.FileUtils;
import com.badbones69.crazycrates.utils.adventure.MsgWrapper;
import net.dehya.ruby.files.FileManager;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConfigConversion {

    public void convertConfig(FileManager fileManager, Path directory) {
        double configVersion = 1.1;

        // The config.yml
        File input = new File(directory + "/config.yml");

        // The renamed file.
        File output = new File(directory + "/config-v1.yml");

        // The old configuration of config.yml
        YamlConfiguration yamlConfiguration = null;

        try {
            if (input.exists()) yamlConfiguration = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (yamlConfiguration == null) return;

        if (yamlConfiguration.getString("Settings.Config-Version") != null) {
            yamlConfiguration.set("Settings.Config-Version", 1.0);

            MsgWrapper.send(yamlConfiguration.getString("Settings.Config-Version"));

            try {
                yamlConfiguration.save(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (yamlConfiguration.getDouble("Settings.Config-Version") >= configVersion || yamlConfiguration.getDouble("settings.config-version") >= configVersion) {
            MsgWrapper.send("<#11e092>" + input.getName() + " <#E0115F>is up to date");
            return;
        }

        // Rename the file to the output file.
        if (input.renameTo(output)) MsgWrapper.send("<#E0115F>Renamed " + input.getName() + " <#E0115F>to <#11e092>" + output.getName() + ".");

        // The configuration of the output file.
        YamlConfiguration secondConfiguration = null;

        try {
            if (output.exists()) secondConfiguration = YamlConfiguration.loadConfiguration(output);
        } catch (IOException e) {
            MsgWrapper.send(e.getMessage());
        }

        if (secondConfiguration == null) return;

        ConfigurationSection msgSection = secondConfiguration.createSection("Settings");

        // All the values of the old file.

        final boolean knockBack = msgSection.getBoolean("KnockBack");

        final boolean physAcceptsVirtual = msgSection.getBoolean("Physical-Accepts-Virtual-Keys");
        final boolean physAcceptsPhys = msgSection.getBoolean("Physical-Accepts-Physical-Keys");
        final boolean virtualAcceptsPhys = msgSection.getBoolean("Virtual-Accepts-Physical-Keys");
        final boolean giveVirtualKeysInventoryMessage = msgSection.getBoolean("Give-Virtual-Keys-When-Inventory-Full-Message");
        final boolean giveVirtualKeysInventory = msgSection.getBoolean("Give-Virtual-Keys-When-Inventory-Full");

        // TODO() Move this to per crate.
        final String needKeySound = msgSection.getString("Need-Key-Sound");

        // TODO() Move this to QuadCrate type.
        final int quadCrateTimer = msgSection.getInt("QuadCrate.Timer");

        final List<String> disabledWorlds = msgSection.getStringList("DisabledWorlds");

        // TODO() Move this to its own configuration file.
        final String menuName = msgSection.getString("Preview.Buttons.Menu.Name");
        final String menuItem = msgSection.getString("Preview.Buttons.Menu.Item");
        final List<String> menuLore = msgSection.getStringList("Preview.Buttons.Menu.Lore");

        final String nextName = msgSection.getString("Preview.Buttons.Next.Name");
        final String nextItem = msgSection.getString("Preview.Buttons.Next.Item");
        final List<String> nextLore = msgSection.getStringList("Preview.Buttons.Next.Lore");

        final String backName = msgSection.getString("Preview.Buttons.Back.Name");
        final String backItem = msgSection.getString("Preview.Buttons.Back.Item");
        final List<String> backLore = msgSection.getStringList("Preview.Buttons.Back.Lore");

        final boolean fillerToggle = msgSection.getBoolean("Filler.Toggle");
        final String fillerItem = msgSection.getString("Filler.Item");
        final String fillerName = msgSection.getString("Filler.Name");
        final List<String> fillerLore = msgSection.getStringList("Filler.Lore");

        final List<String> guiCustomizer = msgSection.getStringList("GUI-Customizer");

        org.simpleyaml.configuration.file.YamlConfiguration configuration = Config.getConfiguration(fileManager);

        if (configuration == null) return;

        ConfigurationSection settingsSection = configuration.createSection("settings");

        ConfigurationSection crateSettingsSection = configuration.createSection("crate-settings");

        final String prefix = msgSection.getString("Prefix");
        //final int version = msgSection.getInt("Config-Version");
        final boolean updateChecker = msgSection.getBoolean("Update-Checker");
        final boolean toggleMetrics = msgSection.getBoolean("Toggle-Metrics");

        settingsSection.addDefault("prefix", prefix);
        settingsSection.addDefault("config-version", configVersion);
        settingsSection.addDefault("update-checker", updateChecker);
        settingsSection.addDefault("toggle-metrics", toggleMetrics);

        final boolean crateLogFile = msgSection.getBoolean("Crate-Actions.Log-File");
        final boolean crateLogConsole = msgSection.getBoolean("Crate-Actions.Log-Console");

        crateSettingsSection.addDefault("crate-actions.log-to-file", crateLogFile);
        crateSettingsSection.addDefault("crate-actions.log-to-console", crateLogConsole);

        final boolean enableCrateMenu = msgSection.getBoolean("Enable-Crate-Menu");

        crateSettingsSection.addDefault("preview-menu.toggle", enableCrateMenu);

        final String invName = msgSection.getString("InventoryName");
        final int invSize = secondConfiguration.getInt("Settings.InventorySize");

        crateSettingsSection.addDefault("preview-menu.name", invName);
        crateSettingsSection.addDefault("preview-menu.size", invSize);

        crateSettingsSection.addDefault("knock-back", knockBack);
        crateSettingsSection.addDefault("keys.physical-accepts-virtual-keys", physAcceptsVirtual);
        crateSettingsSection.addDefault("keys.physical-accepts-physical-keys", physAcceptsPhys);
        crateSettingsSection.addDefault("keys.virtual-accepts-physical-keys", virtualAcceptsPhys);

        crateSettingsSection.addDefault("keys.inventory-not-empty.give-virtual-keys-message", giveVirtualKeysInventoryMessage);
        crateSettingsSection.addDefault("keys.inventory-not-empty.give-virtual-keys", giveVirtualKeysInventory);

        crateSettingsSection.addDefault("keys.key-sound.name", needKeySound);

        crateSettingsSection.addDefault("quad-crate.timer", quadCrateTimer);

        crateSettingsSection.addDefault("disabled-worlds.worlds", disabledWorlds);

        ConfigurationSection guiSettingsSection = configuration.createSection("gui-settings");

        guiSettingsSection.addDefault("filler-items.toggle", fillerToggle);
        guiSettingsSection.addDefault("filler-items.item", fillerItem);
        guiSettingsSection.addDefault("filler-items.name", fillerName);
        guiSettingsSection.addDefault("filler-items.lore", fillerLore);

        guiSettingsSection.addDefault("buttons.menu.item", menuItem);
        guiSettingsSection.addDefault("buttons.menu.name", menuName);
        guiSettingsSection.addDefault("buttons.menu.lore", menuLore);

        guiSettingsSection.addDefault("buttons.next.item", nextItem);
        guiSettingsSection.addDefault("buttons.next.name", nextName);
        guiSettingsSection.addDefault("buttons.next.lore", nextLore);

        guiSettingsSection.addDefault("buttons.back.item", backItem);
        guiSettingsSection.addDefault("buttons.back.name", backName);
        guiSettingsSection.addDefault("buttons.back.lore", backLore);

        guiSettingsSection.addDefault("customizer", guiCustomizer);

        FileUtils.copyFile(input, output, configuration, directory);
    }
}