package com.badbones69.crazycrates.configs;

import com.badbones69.crazycrates.api.FileManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigConvert {

    private final FileConfiguration config = FileManager.Files.CONFIG.getFile();
    private final FileConfiguration messages = FileManager.Files.MESSAGES.getFile();

    // Config Options
    private final String prefix = config.getString("Settings.Prefix");
    private final int version = config.getInt("Settings.Config-Version");
    private final boolean updateChecker = config.getBoolean("Settings.Update-Checker");
    private final boolean toggleMetrics = config.getBoolean("Settings.Toggle-Metrics");
    private final boolean enableCrateMenu = config.getBoolean("Settings.Enable-Crate-Menu");
    private final boolean crateLogFile = config.getBoolean("Settings.Crate-Actions.Log-File");
    private final boolean crateLogConsole = config.getBoolean("Settings.Crate-Actions.Log-Console");

    private final String invName = config.getString("Settings.InventoryName");
    private final int invSize = config.getInt("Settings.InventorySize");

    private final boolean knockBack = config.getBoolean("Settings.KnockBack");

    private final boolean physAcceptsVirtual = config.getBoolean("Settings.Physical-Accepts-Virtual-Keys");
    private final boolean physAcceptsPhys = config.getBoolean("Settings.Physical-Accepts-Physical-Keys");
    private final boolean virtualAcceptsPhys = config.getBoolean("Settings.Virtual-Accepts-Physical-Keys");
    private final boolean giveVirtualKeysInventoryMessage = config.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");
    private final boolean giveVirtualKeysInventory = config.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");

    // TODO() Move this to per crate.
    private final String needKeySound = config.getString("Settings.Need-Key-Sound");

    // TODO() Move this to QuadCrate type.
    private final int quadCrateTimer = config.getInt("Settings.QuadCrate.Timer");

    private final List<String> disabledWorlds = config.getStringList("Settings.DisabledWorlds");

    // TODO() Move this to it's own configuration file.
    private final String menuName = config.getString("Settings.Preview.Buttons.Menu.Name");
    private final String menuItem = config.getString("Settings.Preview.Buttons.Menu.Item");
    private final List<String> menuLore = config.getStringList("Settings.Preview.Buttons.Menu.Lore");

    private final String nextName = config.getString("Settings.Preview.Buttons.Next.Name");
    private final String nextItem = config.getString("Settings.Preview.Buttons.Next.Item");
    private final List<String> nextLore = config.getStringList("Settings.Preview.Buttons.Next.Lore");

    private final String backName = config.getString("Settings.Preview.Buttons.Back.Name");
    private final String backItem = config.getString("Settings.Preview.Buttons.Back.Item");
    private final List<String> backLore = config.getStringList("Settings.Preview.Buttons.Back.Lore");

    private final boolean fillerToggle = config.getBoolean("Settings.Filler.Toggle");
    private final String fillerItem = config.getString("Settings.Filler.Item");
    private final String fillerName = config.getString("Settings.Filler.Name");
    private final List<String> fillerLore = config.getStringList("Settings.Filler.Lore");

    public void convertConfig() {

    }

    public void convertMessages() {

    }
}