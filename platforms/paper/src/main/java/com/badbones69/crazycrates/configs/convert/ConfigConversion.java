package com.badbones69.crazycrates.configs.convert;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.badbones69.crazycrates.CrazyCrates;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigConversion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    public void convertConfig() {
        File file = new File(this.plugin.getDataFolder() + "/config.yml");

        File secondFile = new File(this.plugin.getDataFolder() + "/config-v1.yml");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        if (yamlConfiguration.getString("Settings.Config-Version") == null && !secondFile.exists()) {
            this.plugin.getLogger().warning("Could not find Config-Version, I am assuming configurations have been converted.");
            return;
        }

        if (file.renameTo(secondFile)) this.plugin.getLogger().warning("Renamed " + file.getName() + " to config-v1.yml");

        YamlConfiguration secondConfiguration = YamlConfiguration.loadConfiguration(secondFile);

        // Config Options
        final String prefix = secondConfiguration.getString("Settings.Prefix");
        // final int version = secondConfiguration.getInt("Settings.Config-Version");
        final boolean updateChecker = secondConfiguration.getBoolean("Settings.Update-Checker");
        final boolean toggleMetrics = secondConfiguration.getBoolean("Settings.Toggle-Metrics");
        final boolean enableCrateMenu = secondConfiguration.getBoolean("Settings.Enable-Crate-Menu");
        final boolean crateLogFile = secondConfiguration.getBoolean("Settings.Crate-Actions.Log-File");
        final boolean crateLogConsole = secondConfiguration.getBoolean("Settings.Crate-Actions.Log-Console");

        final String invName = secondConfiguration.getString("Settings.InventoryName");
        final int invSize = secondConfiguration.getInt("Settings.InventorySize");

        final boolean knockBack = secondConfiguration.getBoolean("Settings.KnockBack");

        final boolean physAcceptsVirtual = secondConfiguration.getBoolean("Settings.Physical-Accepts-Virtual-Keys");
        final boolean physAcceptsPhys = secondConfiguration.getBoolean("Settings.Physical-Accepts-Physical-Keys");
        final boolean virtualAcceptsPhys = secondConfiguration.getBoolean("Settings.Virtual-Accepts-Physical-Keys");
        final boolean giveVirtualKeysInventoryMessage = secondConfiguration.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");
        final boolean giveVirtualKeysInventory = secondConfiguration.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");

        // TODO() Move this to per crate.
        final String needKeySound = secondConfiguration.getString("Settings.Need-Key-Sound");

        // TODO() Move this to QuadCrate type.
        final int quadCrateTimer = secondConfiguration.getInt("Settings.QuadCrate.Timer");

        final List<String> disabledWorlds = secondConfiguration.getStringList("Settings.DisabledWorlds");

        // TODO() Move this to it's own configuration file.
        final String menuName = secondConfiguration.getString("Settings.Preview.Buttons.Menu.Name");
        final String menuItem = secondConfiguration.getString("Settings.Preview.Buttons.Menu.Item");
        final List<String> menuLore = secondConfiguration.getStringList("Settings.Preview.Buttons.Menu.Lore");

        final String nextName = secondConfiguration.getString("Settings.Preview.Buttons.Next.Name");
        final String nextItem = secondConfiguration.getString("Settings.Preview.Buttons.Next.Item");
        final List<String> nextLore = secondConfiguration.getStringList("Settings.Preview.Buttons.Next.Lore");

        final String backName = secondConfiguration.getString("Settings.Preview.Buttons.Back.Name");
        final String backItem = secondConfiguration.getString("Settings.Preview.Buttons.Back.Item");
        final List<String> backLore = secondConfiguration.getStringList("Settings.Preview.Buttons.Back.Lore");

        final boolean fillerToggle = secondConfiguration.getBoolean("Settings.Filler.Toggle");
        final String fillerItem = secondConfiguration.getString("Settings.Filler.Item");
        final String fillerName = secondConfiguration.getString("Settings.Filler.Name");
        final List<String> fillerLore = secondConfiguration.getStringList("Settings.Filler.Lore");

        final List<String> guiCustomizer = secondConfiguration.getStringList("Settings.GUI-Customizer");

        yamlConfiguration.set("settings.prefix", prefix);
        yamlConfiguration.set("settings.update-checker", updateChecker);
        yamlConfiguration.set("settings.toggle-metrics", toggleMetrics);
        yamlConfiguration.set("crate-settings.crate-actions.log-to-file", crateLogFile);
        yamlConfiguration.set("crate-settings.crate-actions.log-to-console", crateLogConsole);

        yamlConfiguration.set("crate-settings.preview-menu.toggle", enableCrateMenu);
        yamlConfiguration.set("crate-settings.preview-menu.name", invName);
        yamlConfiguration.set("crate-settings.preview-menu.size", invSize);

        yamlConfiguration.set("crate-settings.knock-back", knockBack);
        yamlConfiguration.set("crate-settings.keys.physical-accepts-virtual-keys", physAcceptsVirtual);
        yamlConfiguration.set("crate-settings.keys.physical-accepts-physical-keys", physAcceptsPhys);
        yamlConfiguration.set("crate-settings.keys.virtual-accepts-physical-keys", virtualAcceptsPhys);

        yamlConfiguration.set("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", giveVirtualKeysInventoryMessage);
        yamlConfiguration.set("crate-settings.keys.inventory-not-empty.give-virtual-keys", giveVirtualKeysInventory);

        yamlConfiguration.set("crate-settings.keys.key-sound.name", needKeySound);

        yamlConfiguration.set("crate-settings.quad-crate.timer", quadCrateTimer);

        yamlConfiguration.set("crate-settings.disabled-worlds.worlds", disabledWorlds);

        yamlConfiguration.set("gui-settings.filler-items.toggle", fillerToggle);
        yamlConfiguration.set("gui-settings.filler-items.item", fillerItem);
        yamlConfiguration.set("gui-settings.filler-items.name", fillerName);
        yamlConfiguration.set("gui-settings.filler-items.lore", fillerLore);

        yamlConfiguration.set("gui-settings.buttons.menu.item", menuItem);
        yamlConfiguration.set("gui-settings.buttons.menu.name", menuName);
        yamlConfiguration.set("gui-settings.buttons.menu.lore", menuLore);

        yamlConfiguration.set("gui-settings.buttons.next.item", nextItem);
        yamlConfiguration.set("gui-settings.buttons.next.name", nextName);
        yamlConfiguration.set("gui-settings.buttons.next.lore", nextLore);

        yamlConfiguration.set("gui-settings.buttons.back.item", backItem);
        yamlConfiguration.set("gui-settings.buttons.back.name", backName);
        yamlConfiguration.set("gui-settings.buttons.back.lore", backLore);

        yamlConfiguration.set("gui-settings.customizer", guiCustomizer);

        try {
            yamlConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}