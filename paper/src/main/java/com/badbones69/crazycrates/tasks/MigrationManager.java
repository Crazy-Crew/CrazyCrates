package com.badbones69.crazycrates.tasks;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import ch.jalu.configme.resource.YamlFileResourceOptions;
import com.badbones69.crazycrates.CrazyCratesPaper;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.CommandKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.CrateKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.ErrorKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.MiscKeys;
import us.crazycrew.crazycrates.platform.config.impl.messages.PlayerKeys;
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
    private static final CrazyCratesPaper plugin = JavaPlugin.getPlugin(CrazyCratesPaper.class);

    public static void migrate() {
        File directory = new File(plugin.getDataFolder(), "backups");
        directory.mkdirs();

        // Update the config file.
        copyConfig(directory);

        // Update the messages file.
        copyMessages(directory);

        // Grab values from the plugin-config.yml if it even exists.
        copyPluginConfig();
    }

    private static void copyPluginConfig() {
        File input = new File(plugin.getDataFolder(), "plugin-config.yml");

        if (!input.exists()) return;

        YamlConfiguration configuration = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        YamlFileResourceOptions builder = YamlFileResourceOptions.builder().indentationSize(2).build();

        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(new File(plugin.getDataFolder(), "config.yml"), builder)
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
        if (input.delete()) plugin.getLogger().warning("Successfully migrated " + input.getName());
    }

    private static void copyMessages(File directory) {
        // Create the file object.
        File input = new File(plugin.getDataFolder(), "messages.yml");

        // Load the configuration.
        YamlConfiguration old = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        if (old.getString("Messages.No-Teleporting") == null) return;

        // Create the new file object.
        File newFile = new File(directory, "messages-v1.yml");
        if (!newFile.exists()) {
            input.renameTo(newFile);
        }
        
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

            messages.setProperty(ErrorKeys.no_prizes_found, convert(section.getString("No-Prizes-Found", ErrorKeys.no_prizes_found.getDefaultValue())));

            messages.setProperty(ErrorKeys.no_schematics_found, convert(section.getString("No-Schematics-Found", ErrorKeys.no_schematics_found.getDefaultValue())));

            messages.setProperty(ErrorKeys.internal_error, convert(section.getString("Internal-Error", ErrorKeys.internal_error.getDefaultValue())));

            messages.setProperty(ErrorKeys.prize_error, convert(section.getString("Prize-Error", ErrorKeys.prize_error.getDefaultValue())));

            messages.setProperty(PlayerKeys.must_be_a_player, convert(section.getString("Must-Be-A-Player", PlayerKeys.must_be_a_player.getDefaultValue())));

            messages.setProperty(PlayerKeys.must_be_console_sender, convert(section.getString("Must-Be-A-Console-Sender", PlayerKeys.must_be_console_sender.getDefaultValue())));

            messages.setProperty(PlayerKeys.must_be_looking_at_block, convert(section.getString("Must-Be-Looking-At-A-Block", PlayerKeys.must_be_looking_at_block.getDefaultValue())));

            messages.setProperty(PlayerKeys.not_online, convert(section.getString("Not-Online", PlayerKeys.not_online.getDefaultValue())));

            messages.setProperty(PlayerKeys.same_player, convert(section.getString("Same-Player", PlayerKeys.same_player.getDefaultValue())));

            messages.setProperty(PlayerKeys.no_permission, convert(section.getString("No-Permission", PlayerKeys.no_permission.getDefaultValue())));

            messages.setProperty(PlayerKeys.obtaining_keys, convert(section.getString("Obtaining-Keys", PlayerKeys.obtaining_keys.getDefaultValue())));

            messages.setProperty(PlayerKeys.too_close_to_another_player, convert(section.getString("To-Close-To-Another-Player", PlayerKeys.too_close_to_another_player.getDefaultValue())));

            messages.setProperty(CrateKeys.not_a_crate, convert(section.getString("Not-A-Crate", CrateKeys.not_a_crate.getDefaultValue())));

            messages.setProperty(CrateKeys.not_a_number, convert(section.getString("Not-A-Number", CrateKeys.not_a_number.getDefaultValue())));

            messages.setProperty(CrateKeys.required_keys, convert(section.getString("Required-Keys", CrateKeys.required_keys.getDefaultValue())));

            messages.setProperty(CrateKeys.not_on_block, convert(section.getString("Not-On-Block", CrateKeys.not_on_block.getDefaultValue())));

            messages.setProperty(CrateKeys.out_of_time, convert(section.getString("Out-Of-Time", CrateKeys.out_of_time.getDefaultValue())));

            messages.setProperty(CrateKeys.reloaded_forced_out_of_preview, convert(section.getString("Forced-Out-Of-Preview", CrateKeys.reloaded_forced_out_of_preview.getDefaultValue())));

            messages.setProperty(CrateKeys.cannot_set_type, convert(section.getString("Cannot-Set-Menu-Type", CrateKeys.cannot_set_type.getDefaultValue())));

            messages.setProperty(CrateKeys.no_crate_permission, convert(section.getString("No-Crate-Permission", CrateKeys.no_crate_permission.getDefaultValue())));

            messages.setProperty(CrateKeys.preview_disabled, convert(section.getString("Preview-Disabled", CrateKeys.preview_disabled.getDefaultValue())));

            messages.setProperty(CrateKeys.already_opening_crate, convert(section.getString("Already-Opening-Crate", CrateKeys.already_opening_crate.getDefaultValue())));

            messages.setProperty(CrateKeys.crate_in_use, convert(section.getString("Quick-Crate-In-Use", CrateKeys.crate_in_use.getDefaultValue())));

            messages.setProperty(CrateKeys.cant_be_a_virtual_crate, convert(section.getString("Cant-Be-A-Virtual-Crate", CrateKeys.cant_be_a_virtual_crate.getDefaultValue())));

            messages.setProperty(CrateKeys.needs_more_room, convert(section.getString("Needs-More-Room", CrateKeys.needs_more_room.getDefaultValue())));

            messages.setProperty(CrateKeys.world_disabled, convert(section.getString("World-Disabled", CrateKeys.world_disabled.getDefaultValue())));

            List<String> lines = new ArrayList<>();

            section.getStringList("Created-Physical-Crate").forEach(line -> lines.add(convert(line)));

            messages.setProperty(CrateKeys.created_physical_crate, lines);

            messages.setProperty(CrateKeys.removed_physical_crate, convert(section.getString("Removed-Physical-Crate", CrateKeys.removed_physical_crate.getDefaultValue())));

            messages.setProperty(CommandKeys.opened_a_crate, convert(section.getString("Opened-A-Crate", CommandKeys.opened_a_crate.getDefaultValue())));

            messages.setProperty(CommandKeys.gave_a_player_keys, convert(section.getString("Given-A-Player-Keys", CommandKeys.gave_a_player_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.cannot_give_player_keys, convert(section.getString("Cannot-Give-Player-Keys", CommandKeys.cannot_give_player_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.given_everyone_keys, convert(section.getString("Given-Everyone-Keys", CommandKeys.given_everyone_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.given_offline_player_keys, convert(section.getString("Given-Offline-Player-Keys", CommandKeys.given_offline_player_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.take_players_keys, convert(section.getString("Take-A-Player-Keys", CommandKeys.take_players_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.cannot_take_keys, convert(section.getString("Cannot-Take-Keys", CommandKeys.cannot_take_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.take_offline_player_keys, convert(section.getString("Take-Offline-Player-Keys", CommandKeys.take_offline_player_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.no_item_in_hand, convert(section.getString("No-Item-In-Hand", CommandKeys.no_item_in_hand.getDefaultValue())));

            messages.setProperty(CommandKeys.added_item_with_editor, convert(section.getString("Added-Item-With-Editor", CommandKeys.added_item_with_editor.getDefaultValue())));

            messages.setProperty(CommandKeys.reloaded_plugin, convert(section.getString("Reload", CommandKeys.reloaded_plugin.getDefaultValue())));

            messages.setProperty(CommandKeys.transfer_not_enough_keys, convert(section.getString("Transfer-Keys.Not-Enough-Keys", CommandKeys.transfer_not_enough_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.transfer_sent_keys, convert(section.getString("Transfer-Keys.Transferred-Keys", CommandKeys.transfer_sent_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.transfer_received_keys, convert(section.getString("Transfer-Keys.Received-Transferred-Keys", CommandKeys.transfer_received_keys.getDefaultValue())));

            messages.setProperty(CommandKeys.no_virtual_keys, convert(section.getString("Keys.Personal.No-Virtual-Keys", CommandKeys.no_virtual_keys.getDefaultValue())));

            List<String> personalHeader = new ArrayList<>();

            section.getStringList("Keys.Personal.Header").forEach(line -> personalHeader.add(convert(line)));

            messages.setProperty(CommandKeys.virtual_keys_header, personalHeader);

            messages.setProperty(CommandKeys.other_player_no_keys, convert(section.getString("Keys.Personal.Other-Player.No-Virtual-Keys", CommandKeys.other_player_no_keys.getDefaultValue())));

            List<String> otherHeader = new ArrayList<>();

            section.getStringList("Keys.Other-Player.Header").forEach(line -> otherHeader.add(convert(line)));

            messages.setProperty(CommandKeys.other_player_header, otherHeader);

            messages.setProperty(CommandKeys.per_crate, convert(section.getString("Keys.Per-Crate", CommandKeys.per_crate.getDefaultValue())));

            messages.setProperty(CommandKeys.help, section.getStringList("Help"));

            messages.setProperty(CommandKeys.admin_help, section.getStringList("Admin-Help"));
        }

        messages.save();
    }

    private static void copyConfig(File directory) {
        // Create the file object.
        File input = new File(plugin.getDataFolder(), "config.yml");

        // Load the configuration.
        YamlConfiguration old = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        String settings = "Settings.";

        if (old.getString(settings + "Enable-Crate-Menu") == null) return;

        // Create the new file object.
        File newFile = new File(directory, "config-v1.yml");
        if (!newFile.exists()) {
            input.renameTo(newFile);
        }

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

    private static String colors(String message) {
        return message.replaceAll("&0", "<black>")
                .replaceAll("&1", "<dark_blue>")
                .replaceAll("&2", "<dark_green>")
                .replaceAll("&3", "<dark_aqua>")
                .replaceAll("&4", "<dark_red>")
                .replaceAll("&5", "<dark_purple>")
                .replaceAll("&6", "<gold>")
                .replaceAll("&7", "<gray>")
                .replaceAll("&8", "<dark_gray>")
                .replaceAll("&9", "<blue>")
                .replaceAll("&a", "<green>")
                .replaceAll("&b", "<aqua>")
                .replaceAll("&c", "<red>")
                .replaceAll("&d", "<light_purple>")
                .replaceAll("&e", "<yellow>")
                .replaceAll("&f", "<white>")
                .replaceAll("&k", "<obfuscated>").replaceAll("&l", "<bold>")
                .replaceAll("&m", "<strikethrough>").replaceAll("&n", "<underline>")
                .replaceAll("&o", "<italic>").replaceAll("&r", "<reset>");
    }

    private static String convert(String message) {
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