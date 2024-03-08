package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.common.config.types.messages.CommandKeys;
import com.badbones69.crazycrates.common.config.types.messages.CrateKeys;
import com.badbones69.crazycrates.common.config.types.messages.ErrorKeys;
import com.badbones69.crazycrates.common.config.types.messages.MiscKeys;
import com.badbones69.crazycrates.common.config.types.messages.PlayerKeys;
import org.bukkit.configuration.ConfigurationSection;
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

        // Update the messages file.
        copyMessages();

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

        config.setProperty(ConfigKeys.verbose_logging, configuration.getBoolean("verbose_logging", ConfigKeys.verbose_logging.getDefaultValue()));
        config.setProperty(ConfigKeys.toggle_metrics, configuration.getBoolean("toggle_metrics", ConfigKeys.toggle_metrics.getDefaultValue()));
        config.setProperty(ConfigKeys.command_prefix, configuration.getString("command_prefix", ConfigKeys.command_prefix.getDefaultValue()));
        config.setProperty(ConfigKeys.console_prefix, configuration.getString("console_prefix", ConfigKeys.console_prefix.getDefaultValue()));

        // Save to file.
        config.save();

        // Delete old file.
        if (input.delete()) this.plugin.getLogger().warning("Successfully migrated " + input.getName());
    }

    private void copyMessages() {
        // Create the file object.
        File input = new File(this.plugin.getDataFolder(), "messages.yml");

        // Load the configuration.
        YamlConfiguration old = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        if (old.getString("Messages.No-Teleporting") == null) return;

        // Create the new file object.
        File newFile = new File(this.plugin.getDataFolder(), "messages-backup.yml");
        // Rename it.
        input.renameTo(newFile);

        YamlConfiguration configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(newFile)).join();

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager messages = SettingsManagerBuilder
                .withYamlFile(input, builder)
                .useDefaultMigrationService()
                .configurationData(MiscKeys.class, ErrorKeys.class, PlayerKeys.class, CrateKeys.class, CommandKeys.class)
                .create();

        ConfigurationSection section = configuration.getConfigurationSection("Messages");

        if (section != null) {
            messages.setProperty(MiscKeys.unknown_command, convert(section.getString("Unknown-Command", MiscKeys.unknown_command.getDefaultValue())));

            messages.setProperty(MiscKeys.unknown_command, convert(section.getString("Unknown-Command", MiscKeys.unknown_command.getDefaultValue())));

            messages.setProperty(MiscKeys.no_teleporting, convert(section.getString("No-Teleporting", MiscKeys.no_teleporting.getDefaultValue())));

            messages.setProperty(MiscKeys.no_commands_while_using_crate, convert(section.getString("No-Commands-While-In-Crate", MiscKeys.no_commands_while_using_crate.getDefaultValue())));

            messages.setProperty(MiscKeys.no_keys, convert(section.getString("No-Key", MiscKeys.no_keys.getDefaultValue())));

            messages.setProperty(MiscKeys.no_virtual_key, convert(section.getString("No-Virtual-Key", MiscKeys.no_virtual_key.getDefaultValue())));

            messages.setProperty(MiscKeys.correct_usage, convert(section.getString("Correct-Usage", MiscKeys.correct_usage.getDefaultValue())));

            messages.setProperty(MiscKeys.feature_disabled, convert(section.getString("Feature-Disabled", MiscKeys.feature_disabled.getDefaultValue())));
        }

        messages.save();
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

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(input, builder)
                .useDefaultMigrationService()
                .configurationData(ConfigKeys.class)
                .create();

        ConfigurationSection section = configuration.getConfigurationSection("Settings");

        if (section != null) {
            if (section.contains("Prefix")) {
                config.setProperty(ConfigKeys.command_prefix, section.getString("Prefix", ConfigKeys.command_prefix.getDefaultValue()));
            }

            // If these are prent for whatever reason.
            if (configuration.contains("Toggle-Metrics")) {
                config.setProperty(ConfigKeys.toggle_metrics, section.getBoolean("Toggle-Metrics", ConfigKeys.toggle_metrics.getDefaultValue()));
            }

            config.setProperty(ConfigKeys.enable_crate_menu, section.getBoolean("Enable-Crate-Menu", ConfigKeys.enable_crate_menu.getDefaultValue()));

            config.setProperty(ConfigKeys.show_quickcrate_item, section.getBoolean("Show-QuickCrate-Item", ConfigKeys.show_quickcrate_item.getDefaultValue()));

            config.setProperty(ConfigKeys.log_to_file, section.getBoolean("Log-File", ConfigKeys.log_to_file.getDefaultValue()));
            config.setProperty(ConfigKeys.log_to_console, section.getBoolean("Log-Console", ConfigKeys.log_to_console.getDefaultValue()));

            config.setProperty(ConfigKeys.inventory_name, section.getString("InventoryName", ConfigKeys.inventory_name.getDefaultValue()));

            config.setProperty(ConfigKeys.inventory_size, section.getInt("InventorySize", ConfigKeys.inventory_size.getDefaultValue()));

            config.setProperty(ConfigKeys.knock_back, section.getBoolean("KnockBack", ConfigKeys.knock_back.getDefaultValue()));

            config.setProperty(ConfigKeys.take_out_of_preview, section.getBoolean("Force-Out-Of-Preview", ConfigKeys.take_out_of_preview.getDefaultValue()));
            config.setProperty(ConfigKeys.send_preview_taken_out_message, section.getBoolean("Force-Out-Of-Preview-Message", ConfigKeys.send_preview_taken_out_message.getDefaultValue()));

            config.setProperty(ConfigKeys.cosmic_crate_timeout, section.getBoolean("Cosmic-Crate-Timeout", ConfigKeys.cosmic_crate_timeout.getDefaultValue()));

            config.setProperty(ConfigKeys.physical_accepts_virtual_keys, section.getBoolean("Physical-Accepts-Virtual-Keys", ConfigKeys.physical_accepts_virtual_keys.getDefaultValue()));
            config.setProperty(ConfigKeys.physical_accepts_physical_keys, section.getBoolean("Physical-Accepts-Physical-Keys", ConfigKeys.physical_accepts_physical_keys.getDefaultValue()));
            config.setProperty(ConfigKeys.virtual_accepts_physical_keys, section.getBoolean("Virtual-Accepts-Physical-Keys", ConfigKeys.virtual_accepts_physical_keys.getDefaultValue()));

            config.setProperty(ConfigKeys.give_virtual_keys_when_inventory_full, section.getBoolean("Give-Virtual-Keys-When-Inventory-Full", ConfigKeys.give_virtual_keys_when_inventory_full.getDefaultValue()));

            config.setProperty(ConfigKeys.notify_player_when_inventory_full, section.getBoolean("Give-Virtual-Keys-When-Inventory-Full-Message", ConfigKeys.notify_player_when_inventory_full.getDefaultValue()));

            config.setProperty(ConfigKeys.need_key_sound_toggle, section.getBoolean("Need-Key-Sound-Toggle", ConfigKeys.need_key_sound_toggle.getDefaultValue()));
            config.setProperty(ConfigKeys.need_key_sound, section.getString("Need-Key-Sound", ConfigKeys.need_key_sound.getDefaultValue()));

            config.setProperty(ConfigKeys.quad_crate_timer, section.getInt("QuadCrate.Timer", ConfigKeys.quad_crate_timer.getDefaultValue()));

            config.setProperty(ConfigKeys.disabled_worlds, section.getStringList("DisabledWorlds"));

            String path = settings + "Preview.Buttons.";

            config.setProperty(ConfigKeys.menu_button_item, section.getString(path + "Menu.Item", ConfigKeys.menu_button_item.getDefaultValue()));
            config.setProperty(ConfigKeys.menu_button_name, convert(section.getString(path + "Menu.Name", ConfigKeys.menu_button_name.getDefaultValue())));

            List<String> menuLore = new ArrayList<>();

            section.getStringList(path + "Menu.Lore").forEach(line -> menuLore.add(convert(line)));

            config.setProperty(ConfigKeys.menu_button_lore, menuLore);

            config.setProperty(ConfigKeys.menu_button_override, section.getBoolean(path + "Menu.override.toggle", ConfigKeys.menu_button_override.getDefaultValue()));

            List<String> commands = new ArrayList<>();

            section.getStringList(path + "Menu.override.list").forEach(line -> commands.add(convert(line)));

            config.setProperty(ConfigKeys.menu_button_command_list, commands);

            config.setProperty(ConfigKeys.next_button_item, section.getString(path + "Next.Item", ConfigKeys.next_button_item.getDefaultValue()));
            config.setProperty(ConfigKeys.next_button_name, convert(section.getString(path + "Next.Name", ConfigKeys.next_button_name.getDefaultValue())));

            List<String> nextLore = new ArrayList<>();

            section.getStringList(path + "Next.Lore").forEach(line -> nextLore.add(convert(line)));

            config.setProperty(ConfigKeys.next_button_lore, nextLore);

            config.setProperty(ConfigKeys.back_button_item, section.getString(path + "Back.Item", ConfigKeys.back_button_item.getDefaultValue()));
            config.setProperty(ConfigKeys.back_button_name, convert(section.getString(path + "Back.Name", ConfigKeys.back_button_name.getDefaultValue())));

            List<String> backLore = new ArrayList<>();

            section.getStringList(path + "Back.Lore").forEach(line -> backLore.add(convert(line)));

            config.setProperty(ConfigKeys.back_button_lore, backLore);

            config.setProperty(ConfigKeys.filler_toggle, section.getBoolean("Filler.Toggle", ConfigKeys.filler_toggle.getDefaultValue()));
            config.setProperty(ConfigKeys.filler_item, section.getString("Filler.Item", ConfigKeys.filler_item.getDefaultValue()));
            config.setProperty(ConfigKeys.filler_name, convert(section.getString("Filler.Name", ConfigKeys.filler_name.getDefaultValue())));

            List<String> fillerLore = new ArrayList<>();

            section.getStringList("Filler.Lore").forEach(line -> fillerLore.add(convert(line)));

            config.setProperty(ConfigKeys.filler_lore, fillerLore);

            config.setProperty(ConfigKeys.gui_customizer_toggle, section.getBoolean("GUI-Customizer-Toggle", ConfigKeys.gui_customizer_toggle.getDefaultValue()));

            List<String> customizer = new ArrayList<>();

            section.getStringList("GUI-Customizer").forEach(line -> customizer.add(line
                    .replaceAll("Item:", "item:")
                    .replaceAll("Slot:", "slot:")
                    .replaceAll("Name:", "name:")
                    .replaceAll("Lore:", "lore:")
                    .replaceAll("Glowing:" , "glowing:")
                    .replaceAll("Player:", "player:")
                    .replaceAll("Unbreakable-Item", "unbreakable_item:")
                    .replaceAll("Hide-Item-Flags", "hide_item_flags:")
            ));

            config.setProperty(ConfigKeys.gui_customizer, customizer);
        }

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