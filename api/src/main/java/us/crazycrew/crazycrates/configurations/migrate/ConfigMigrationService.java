package us.crazycrew.crazycrates.configurations.migrate;

import org.simpleyaml.configuration.file.YamlConfiguration;
import us.crazycrew.crazycore.CrazyCore;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Description: Migrate old values to new values manually.
 */
public class ConfigMigrationService {

    private final File path = CrazyCore.api().getDirectory().toFile();

    private final String prefix = "Settings.";

    public void convert() {
        // We copy this first.
        copyPluginSettings();

        // We copy this next.
        copyConfigSettings();
    }

    private void copyConfigSettings() {
        File output = new File(path + "/config-v1.yml");

        File input = new File(path + "/config.yml");

        // The old configuration of config.yml.
        YamlConfiguration config = null;
        YamlConfiguration configV1 = null;

        try {
            if (!input.exists()) input.createNewFile();

            config = YamlConfiguration.loadConfiguration(input);

            configV1 = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config == null) return;

        if (config.getString(prefix + "Enable-Crate-Menu") == null && !output.exists()) return;

        input.renameTo(output);

        if (configV1 == null) return;

        boolean updateChecker = configV1.getBoolean(prefix + "Update-Checker");
        boolean toggleMetrics = configV1.getBoolean(prefix + "Toggle-Metrics");
        boolean enableCrateMenu = configV1.getBoolean(prefix + "Enable-Crate-Menu");
        boolean crateLogFile = configV1.getBoolean(prefix + "Crate-Actions.Log-File");
        boolean crateLogConsole = configV1.getBoolean(prefix + "Crate-Actions.Log-Console");

        String invName = configV1.getString(prefix + "InventoryName");
        int invSize = configV1.getInt(prefix + "InventorySize");

        boolean knockBack = configV1.getBoolean(prefix + "KnockBack");

        boolean physAcceptsVirtual = configV1.getBoolean(prefix + "Physical-Accepts-Virtual-Keys");
        boolean physAcceptsPhys = configV1.getBoolean(prefix + "Physical-Accepts-Physical-Keys");
        boolean virtualAcceptsPhys = configV1.getBoolean(prefix + "Virtual-Accepts-Physical-Keys");
        boolean giveVirtualKeysInventoryMessage = configV1.getBoolean(prefix + "Give-Virtual-Keys-When-Inventory-Full-Message");
        boolean giveVirtualKeysInventory = configV1.getBoolean(prefix + "Give-Virtual-Keys-When-Inventory-Full");

        // TODO() Move this to per crate.
        String needKeySound = configV1.getString(prefix + "Need-Key-Sound");

        // TODO() Move this to QuadCrate type.
        int quadCrateTimer = configV1.getInt(prefix + "QuadCrate.Timer");

        List<String> disabledWorlds = configV1.getStringList(prefix + "DisabledWorlds");

        // TODO() Move this to its own configuration file.
        String menuName = configV1.getString(prefix + "Preview.Buttons.Menu.Name");
        String menuItem = configV1.getString(prefix + "Preview.Buttons.Menu.Item");
        List<String> menuLore = configV1.getStringList(prefix + "Preview.Buttons.Menu.Lore");

        String nextName = configV1.getString(prefix + "Preview.Buttons.Next.Name");
        String nextItem = configV1.getString(prefix + "Preview.Buttons.Next.Item");
        List<String> nextLore = configV1.getStringList(prefix + "Preview.Buttons.Next.Lore");

        String backName = configV1.getString(prefix + "Preview.Buttons.Back.Name");
        String backItem = configV1.getString(prefix + "Preview.Buttons.Back.Item");
        List<String> backLore = configV1.getStringList(prefix + "Preview.Buttons.Back.Lore");

        boolean fillerToggle = configV1.getBoolean(prefix + "Filler.Toggle");
        String fillerItem = configV1.getString(prefix + "Filler.Item");
        String fillerName = configV1.getString(prefix + "Filler.Name");
        List<String> fillerLore = configV1.getStringList(prefix + "Filler.Lore");

        List<String> guiCustomizer = configV1.getStringList(prefix + "GUI-Customizer");

        YamlConfiguration other = null;

        try {
            other = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert other != null;
        other.set("settings.prefix", prefix);
        other.set("settings.update-checker", updateChecker);
        other.set("settings.toggle-metrics", toggleMetrics);
        other.set("crate-settings.crate-actions.log-to-file", crateLogFile);
        other.set("crate-settings.crate-actions.log-to-console", crateLogConsole);

        other.set("crate-settings.preview-menu.toggle", enableCrateMenu);
        other.set("crate-settings.preview-menu.name", invName);
        other.set("crate-settings.preview-menu.size", invSize);

        other.set("crate-settings.knock-back", knockBack);
        other.set("crate-settings.keys.physical-accepts-virtual-keys", physAcceptsVirtual);
        other.set("crate-settings.keys.physical-accepts-physical-keys", physAcceptsPhys);
        other.set("crate-settings.keys.virtual-accepts-physical-keys", virtualAcceptsPhys);

        other.set("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", giveVirtualKeysInventoryMessage);
        other.set("crate-settings.keys.inventory-not-empty.give-virtual-keys", giveVirtualKeysInventory);

        other.set("crate-settings.keys.key-sound.name", needKeySound);

        other.set("crate-settings.quad-crate.timer", quadCrateTimer);

        other.set("crate-settings.disabled-worlds.worlds", disabledWorlds);

        other.set("gui-settings.filler-items.toggle", fillerToggle);
        other.set("gui-settings.filler-items.item", fillerItem);
        other.set("gui-settings.filler-items.name", fillerName);

        other.set("gui-settings.filler-items.lore", fillerLore);

        other.set("gui-settings.buttons.menu.item", menuItem);
        other.set("gui-settings.buttons.menu.name", menuName);
        other.set("gui-settings.buttons.menu.lore", menuLore);

        other.set("gui-settings.buttons.next.item", nextItem);
        other.set("gui-settings.buttons.next.name", nextName);
        other.set("gui-settings.buttons.next.lore", nextLore);

        other.set("gui-settings.buttons.back.item", backItem);
        other.set("gui-settings.buttons.back.name", backName);
        other.set("gui-settings.buttons.back.lore", backLore);

        other.set("gui-settings.customizer.items", guiCustomizer);

        try {
            other.save(input);
            output.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyPluginSettings() {
        // The new plugin-settings.yml
        File output = new File(path + "/plugin-settings.yml");

        File input = new File(path + "/config.yml");

        // The old configuration of config.yml.
        YamlConfiguration config = null;

        if (!input.exists()) return;

        try {
            config = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert config != null;
        if (config.getString(prefix + "Prefix") == null) return;

        YamlConfiguration pluginSettings = null;

        try {
            //noinspection ResultOfMethodCallIgnored
            output.createNewFile(); // Create the file if it doesn't exist.
            pluginSettings = YamlConfiguration.loadConfiguration(output); // Load the output.
        } catch (IOException e) {
            e.printStackTrace();
        }

        String oldPrefix = config.getString(prefix + "Prefix");
        boolean oldMetrics = config.getBoolean(prefix + "Toggle-Metrics");
        boolean oldUpdate = config.getBoolean(prefix + "Update-Checker");

        if (pluginSettings == null) return;

        pluginSettings.set(prefix + "prefix.command", oldPrefix);
        pluginSettings.set(prefix + "toggle-metrics", oldMetrics);
        pluginSettings.set(prefix + "update-checker", oldUpdate);

        config.set(prefix + "Prefix", null);
        config.set(prefix + "Toggle-Metrics", null);
        config.set(prefix + "Update-Checker", null);

        try {
            config.save(input);
            pluginSettings.save(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}