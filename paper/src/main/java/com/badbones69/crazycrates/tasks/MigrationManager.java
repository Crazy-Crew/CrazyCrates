package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MigrationManager {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    public void migrate() {
        // Update the config file.
        copyConfig();

        // Grab values from the plugin-config.yml if it even exists.
        copyPluginConfig();
    }

    private void copyPluginConfig() {
        File input = new File(this.plugin.getDataFolder(), "plugin-config.yml");

        if (!input.exists()) return;

        YamlConfiguration configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(new File(this.plugin.getDataFolder(), "config.yml"), builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        config.setProperty(ConfigKeys.verbose_logging, configuration.getBoolean("verbose_logging", false));
        config.setProperty(ConfigKeys.toggle_metrics, configuration.getBoolean("toggle_metrics", false));
        config.setProperty(ConfigKeys.command_prefix, configuration.getString("command_prefix", "&8[&bCrazyCrates&8]: "));
        config.setProperty(ConfigKeys.console_prefix, configuration.getString("console_prefix", "&8[&bCrazyCrates&8] "));

        // Save to file.
        config.save();

        // Delete old file.
        if (input.delete()) this.plugin.getLogger().warning("Successfully migrated " + input.getName());
    }

    private void copyConfig() {
        // Create the file object.
        File input = new File(this.plugin.getDataFolder(), "config.yml");

        // Load the configuration.
        YamlConfiguration old = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        String settings = "Settings.";

        if (old.getString(settings + "Enable-Crate-Menu") == null) return;

        // Create the new file object.
        File newFile = new File(this.plugin.getDataFolder(), "config-backup.yml");
        // Rename it.
        input.renameTo(newFile);

        YamlConfiguration configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(newFile)).join();

        String prefix = configuration.getString(settings + "Prefix", "&8[&bCrazyCrates&8]: ");
        boolean toggleMetrics = configuration.getBoolean(settings + "Toggle-Metrics", true);

        boolean toggleCrateMenu = configuration.getBoolean(settings + "Enable-Crate-Menu", true);
        boolean toggleQuickCrate = configuration.getBoolean(settings + "Show-QuickCrate-Item", true);

        boolean logFile = configuration.getBoolean(settings + "Log-Console", false);
        boolean logConsole = configuration.getBoolean(settings + "Log-File", false);

        String invName = configuration.getString(settings + "InventoryName", "&b&lCrazy &4&lCrates");
        int invSize = configuration.getInt(settings + "InventorySize", 45);

        boolean knockback = configuration.getBoolean(settings + "KnockBack", true);

        boolean previewOut = configuration.getBoolean(settings + "Force-Out-Of-Preview", false);
        boolean previewMsg = configuration.getBoolean(settings + "Force-Out-Of-Preview-Message", false);

        boolean cosmic = configuration.getBoolean(settings + "Cosmic-Crate-Timeout", true);

        boolean physicalVirtual = configuration.getBoolean(settings + "Physical-Accepts-Virtual-Keys", true);
        boolean physicalPhysical = configuration.getBoolean(settings + "Physical-Accepts-Physical-Keys", true);
        boolean virtualPhysical = configuration.getBoolean(settings + "Virtual-Accepts-Physical-Keys", true);

        boolean invFull = configuration.getBoolean(settings + "Give-Virtual-Keys-When-Inventory-Full", false);
        boolean invMsg = configuration.getBoolean(settings + "Give-Virtual-Keys-When-Inventory-Full-Message", false);

        boolean needKeyToggle = configuration.getBoolean(settings + "Need-Key-Sound-Toggle", true);
        String sound = configuration.getString(settings + "Need-Key-Sound", "ENTITY_VILLAGER_NO");

        int timer = configuration.getInt(settings + "QuadCrate.Timer", 300);
        List<String> worlds = configuration.getStringList(settings + "DisabledWorlds");

        String previewPath = settings + "Preview.Buttons.";

        String menuItem = configuration.getString(previewPath + "Menu.Item", "COMPASS");
        String menuName = configuration.getString(previewPath + "Menu.Name", "&7&l>> &c&lMenu &7&l<<");
        List<String> menuLore = configuration.getStringList(previewPath + "Menu.Lore");

        String nextItem = configuration.getString(previewPath + "Next.Item", "COMPASS");
        String nextName = configuration.getString(previewPath + "Next.Name", "&7&l>> &c&lMenu &7&l<<");

        List<String> nextLore = new ArrayList<>();
        configuration.getStringList(previewPath + "Next.Lore").forEach(line -> nextLore.add(convert(line)));

        String backItem = configuration.getString(previewPath + "Back.Item", "COMPASS");
        String backName = configuration.getString(previewPath + "Back.Name", "&7&l>> &c&lMenu &7&l<<");

        List<String> backLore = new ArrayList<>();
        configuration.getStringList(previewPath + "Back.Lore").forEach(line -> backLore.add(convert(line)));

        boolean menuOverride = configuration.getBoolean(previewPath + "Menu.override.toggle", false);
        List<String> menuList = configuration.getStringList(previewPath + "Menu.override.list");

        boolean fillerToggle = configuration.getBoolean(settings + "Filler.Toggle", false);
        String fillerItem = configuration.getString(settings + "Filler.Item", "BLACK_STAINED_GLASS_PANE");
        String fillerName = configuration.getString(settings + "Filler.Name", " ");

        List<String> fillerLore = new ArrayList<>();

        configuration.getStringList(settings + "Filler.Lore").forEach(line -> fillerLore.add(convert(line)));

        boolean customizerToggle = configuration.getBoolean(settings + "GUI-Customizer-Toggle", true);
        List<String> customizer = configuration.getStringList(settings + "GUI-Customizer");

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(input, builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        // If these are prent for whatever reason.
        if (configuration.contains("Settings.Prefix")) {
            config.setProperty(ConfigKeys.command_prefix, prefix);
        }

        // If these are prent for whatever reason.
        if (configuration.contains("Settings.Toggle-Metrics")) {
            config.setProperty(ConfigKeys.toggle_metrics, toggleMetrics);
        }

        config.setProperty(ConfigKeys.enable_crate_menu, toggleCrateMenu);

        config.setProperty(ConfigKeys.show_quickcrate_item, toggleQuickCrate);

        config.setProperty(ConfigKeys.log_to_file, logFile);
        config.setProperty(ConfigKeys.log_to_console, logConsole);

        config.setProperty(ConfigKeys.inventory_name, invName);
        config.setProperty(ConfigKeys.inventory_size, invSize);

        config.setProperty(ConfigKeys.knock_back, knockback);

        config.setProperty(ConfigKeys.take_out_of_preview, previewOut);
        config.setProperty(ConfigKeys.send_preview_taken_out_message, previewMsg);

        config.setProperty(ConfigKeys.cosmic_crate_timeout, cosmic);

        config.setProperty(ConfigKeys.physical_accepts_virtual_keys, physicalVirtual);
        config.setProperty(ConfigKeys.physical_accepts_physical_keys, physicalPhysical);
        config.setProperty(ConfigKeys.virtual_accepts_physical_keys, virtualPhysical);

        config.setProperty(ConfigKeys.give_virtual_keys_when_inventory_full, invFull);
        config.setProperty(ConfigKeys.notify_player_when_inventory_full, invMsg);

        config.setProperty(ConfigKeys.need_key_sound_toggle, needKeyToggle);
        config.setProperty(ConfigKeys.need_key_sound, sound);

        config.setProperty(ConfigKeys.quad_crate_timer, timer);

        config.setProperty(ConfigKeys.disabled_worlds, worlds);

        config.setProperty(ConfigKeys.menu_button_item, menuItem);
        config.setProperty(ConfigKeys.menu_button_name, menuName);

        //todo() loop and replace %page%
        config.setProperty(ConfigKeys.menu_button_lore, menuLore);

        config.setProperty(ConfigKeys.menu_button_override, menuOverride);
        config.setProperty(ConfigKeys.menu_button_command_list, menuList);

        config.setProperty(ConfigKeys.next_button_item, nextItem);
        config.setProperty(ConfigKeys.next_button_name, nextName);

        //todo() loop and replace %page%
        config.setProperty(ConfigKeys.next_button_lore, nextLore);

        config.setProperty(ConfigKeys.back_button_item, backItem);
        config.setProperty(ConfigKeys.back_button_name, backName);

        //todo() loop and replace %page%
        config.setProperty(ConfigKeys.back_button_lore, backLore);

        config.setProperty(ConfigKeys.filler_toggle, fillerToggle);
        config.setProperty(ConfigKeys.filler_item, fillerItem);
        config.setProperty(ConfigKeys.filler_name, fillerName);
        config.setProperty(ConfigKeys.filler_lore, fillerLore);

        config.setProperty(ConfigKeys.gui_customizer_toggle, customizerToggle);
        config.setProperty(ConfigKeys.gui_customizer, customizer);

        config.save();
    }

    private String convert(String message) {
        return message
                .replaceAll("%page%", "{page}")
                .replaceAll("%prefix%", "{prefix}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%crate%", "{crate}")
                .replaceAll("%key%", "{key}")
                .replaceAll("%cratetype%", "{cratetype}")
                .replaceAll("%player%", "{player}")
                .replaceAll("%prize%", "{prize}")
                .replaceAll("%number%", "{number}")
                .replaceAll("%keytype%", "{keytype}")
                .replaceAll("%usage%", "{usage}")
                .replaceAll("%key-amount%", "{key_amount}")
                .replaceAll("%amount%", "{amount}")
                .replaceAll("%id%", "{id}")
                .replaceAll("%crates_opened%", "{crates_opened}");
    }
}