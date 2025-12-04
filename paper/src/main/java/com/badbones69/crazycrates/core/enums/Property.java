package com.badbones69.crazycrates.core.enums;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.core.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.core.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.core.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.core.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.core.config.impl.messages.PlayerKeys;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public enum Property {

    enable_crate_menu(ConfigKeys.enable_crate_menu, newProperty("Settings.Enable-Crate-Menu", ConfigKeys.enable_crate_menu.getDefaultValue()), false),

    show_quickcrate(ConfigKeys.show_quickcrate_item, newProperty("Settings.Show-QuickCrate-Item", ConfigKeys.show_quickcrate_item.getDefaultValue()), false),

    inventory_name(ConfigKeys.inventory_name, newProperty("Settings.InventoryName", ConfigKeys.inventory_name.getDefaultValue())),
    inventory_size(ConfigKeys.inventory_rows, newProperty("Settings.InventorySize", ConfigKeys.inventory_rows.getDefaultValue()), 1),

    knockback(ConfigKeys.knock_back, newProperty("Settings.KnockBack", ConfigKeys.knock_back.getDefaultValue()), false),

    force_out_of_preview(ConfigKeys.take_out_of_preview, newProperty("Settings.Force-Out-Of-Preview", ConfigKeys.take_out_of_preview.getDefaultValue()), false),
    send_preview_message(ConfigKeys.send_preview_taken_out_message, newProperty("Settings.Force-Out-Of-Preview-Message", ConfigKeys.send_preview_taken_out_message.getDefaultValue()), false),

    cosmic_crate_timeout(ConfigKeys.cosmic_crate_timeout, newProperty("Settings.Cosmic-Crate-Timeout", ConfigKeys.cosmic_crate_timeout.getDefaultValue()), false),

    physical_accepts_virtual_keys(ConfigKeys.physical_accepts_virtual_keys, newProperty("Settings.Physical-Accepts-Virtual-Keys", ConfigKeys.physical_accepts_virtual_keys.getDefaultValue()), false),
    physical_accepts_physical_keys(ConfigKeys.physical_accepts_physical_keys, newProperty("Settings.Physical-Accepts-Physical-Keys", ConfigKeys.physical_accepts_physical_keys.getDefaultValue()), false),
    virtual_accepts_physical_keys(ConfigKeys.virtual_accepts_physical_keys, newProperty("Settings.Virtual-Accepts-Physical-Keys", ConfigKeys.virtual_accepts_physical_keys.getDefaultValue()), false),

    give_virtual_keys_when_inventory_full(ConfigKeys.give_virtual_keys_when_inventory_full, newProperty("Settings.Give-Virtual-Keys-When-Inventory-Full", ConfigKeys.give_virtual_keys_when_inventory_full.getDefaultValue()), false),
    give_virtual_keys_when_inventory_full_message(ConfigKeys.send_preview_taken_out_message, newProperty("Settings.Give-Virtual-Keys-When-Inventory-Full-Message", ConfigKeys.send_preview_taken_out_message.getDefaultValue()), false),

    need_key_sound_toggle(ConfigKeys.need_key_sound_toggle, newProperty("Settings.Need-Key-Sound-Toggle", ConfigKeys.need_key_sound_toggle.getDefaultValue()), false),
    need_key_sound(ConfigKeys.need_key_sound, newProperty("Settings.Need-Key-Sound", ConfigKeys.need_key_sound.getDefaultValue())),

    quadcrate_timer(ConfigKeys.quad_crate_timer, newProperty("Settings.QuadCrate.Timer", ConfigKeys.quad_crate_timer.getDefaultValue()), 1),

    disabled_worlds(ConfigKeys.disabled_worlds, newListProperty("Settings.DisabledWorlds", ConfigKeys.disabled_worlds.getDefaultValue()), Collections.emptyList()),

    menu_button_override(ConfigKeys.menu_button_override, newProperty("Settings.Preview.Buttons.Menu.override.toggle", ConfigKeys.menu_button_override.getDefaultValue()), false),
    menu_button_commands(ConfigKeys.menu_button_command_list, newListProperty("Settings.Preview.Buttons.Menu.override.list", ConfigKeys.menu_button_command_list.getDefaultValue()), Collections.emptyList()),
    menu_button_name(ConfigKeys.menu_button_name, newProperty("Settings.Preview.Buttons.Menu.Name", ConfigKeys.menu_button_name.getDefaultValue())),
    menu_button_item(ConfigKeys.menu_button_item, newProperty("Settings.Preview.Buttons.Menu.Item", ConfigKeys.menu_button_item.getDefaultValue())),
    menu_button_lore(ConfigKeys.menu_button_lore, newListProperty("Settings.Preview.Buttons.Menu.Lore", ConfigKeys.menu_button_lore.getDefaultValue()), Collections.emptyList()),

    next_button_name(ConfigKeys.next_button_name, newProperty("Settings.Preview.Buttons.Next.Name", ConfigKeys.next_button_name.getDefaultValue())),
    next_button_item(ConfigKeys.next_button_item, newProperty("Settings.Preview.Buttons.Next.Item", ConfigKeys.next_button_item.getDefaultValue())),
    next_button_lore(ConfigKeys.next_button_lore, newListProperty("Settings.Preview.Buttons.Next.Lore", ConfigKeys.next_button_lore.getDefaultValue()), Collections.emptyList()),

    back_button_name(ConfigKeys.back_button_name, newProperty("Settings.Preview.Buttons.Back.Name", ConfigKeys.back_button_name.getDefaultValue())),
    back_button_item(ConfigKeys.back_button_item, newProperty("Settings.Preview.Buttons.Back.Item", ConfigKeys.back_button_item.getDefaultValue())),
    back_button_lore(ConfigKeys.back_button_lore, newListProperty("Settings.Preview.Buttons.Back.Lore", ConfigKeys.back_button_lore.getDefaultValue()), Collections.emptyList()),

    filler_toggle(ConfigKeys.filler_toggle, newProperty("Settings.Filler.Toggle", ConfigKeys.filler_toggle.getDefaultValue()), false),
    filler_item(ConfigKeys.filler_item, newProperty("Settings.Filler.Item", ConfigKeys.filler_item.getDefaultValue())),
    filler_name(ConfigKeys.filler_name, newProperty("Settings.Filler.Name", ConfigKeys.filler_name.getDefaultValue())),
    filler_lore(ConfigKeys.filler_lore, newListProperty("Settings.Filler.Lore", ConfigKeys.filler_lore.getDefaultValue()), Collections.emptyList()),

    gui_customizer_toggle(ConfigKeys.gui_customizer_toggle, newProperty("Settings.GUI-Customizer-Toggle", ConfigKeys.gui_customizer_toggle.getDefaultValue()), false),
    gui_customizer_lore(ConfigKeys.gui_customizer, newListProperty("Settings.GUI-Customizer", ConfigKeys.gui_customizer.getDefaultValue()), Collections.emptyList()),

    unknown_command(MiscKeys.unknown_command, newProperty("Messages.Unknown-Command", MiscKeys.unknown_command.getDefaultValue())),
    no_teleporting(MiscKeys.no_teleporting, newProperty("Messages.No-Teleporting", MiscKeys.no_teleporting.getDefaultValue())),
    no_commands_while_in_crate(MiscKeys.no_commands_while_using_crate, newProperty("Messages.No-Commands-While-In-Crate", MiscKeys.no_commands_while_using_crate.getDefaultValue())),
    no_key(MiscKeys.no_keys, newProperty("Messages.No-Key", MiscKeys.no_keys.getDefaultValue())),
    no_virtual_key(MiscKeys.no_virtual_key, newProperty("Messages.No-Virtual-Key", MiscKeys.no_virtual_key.getDefaultValue())),
    correct_usage(MiscKeys.correct_usage, newProperty("Messages.Correct-Usage", MiscKeys.correct_usage.getDefaultValue())),
    feature_disabled(MiscKeys.feature_disabled, newProperty("Messages.Feature-Disabled", MiscKeys.feature_disabled.getDefaultValue())),
    no_prizes_found(ErrorKeys.no_prizes_found, newProperty("Messages.No-Prizes-Found", ErrorKeys.no_prizes_found.getDefaultValue())),
    no_schematics_found(ErrorKeys.no_schematics_found, newProperty("Messages.No-Schematics-Found", ErrorKeys.no_schematics_found.getDefaultValue())),
    internal_error(ErrorKeys.internal_error, newProperty("Messages.Internal-Error", ErrorKeys.internal_error.getDefaultValue())),
    inventory_full(PlayerKeys.inventory_not_empty, newProperty("Messages.Inventory-Full", PlayerKeys.inventory_not_empty.getDefaultValue())),
    prize_error(ErrorKeys.prize_error, newProperty("Messages.Prize-Error", ErrorKeys.prize_error.getDefaultValue())),
    must_be_player(PlayerKeys.must_be_a_player, newProperty("Messages.Must-Be-A-Player", PlayerKeys.must_be_a_player.getDefaultValue())),
    must_be_console(PlayerKeys.must_be_console_sender, newProperty("Messages.Must-Be-A-Console-Sender", PlayerKeys.must_be_console_sender.getDefaultValue())),
    must_be_looking_at_block(PlayerKeys.must_be_looking_at_block, newProperty("Messages.Must-Be-Looking-At-A-Block", PlayerKeys.must_be_looking_at_block.getDefaultValue())),
    not_online(PlayerKeys.not_online, newProperty("Messages.Not-Online", PlayerKeys.not_online.getDefaultValue())),
    same_player(PlayerKeys.same_player, newProperty("Messages.Same-Player", PlayerKeys.same_player.getDefaultValue())),
    no_permission(PlayerKeys.no_permission, newProperty("Messages.No-Permission", PlayerKeys.no_permission.getDefaultValue())),
    obtaining_keys(PlayerKeys.obtaining_keys, newProperty("Messages.Obtaining-Keys", PlayerKeys.obtaining_keys.getDefaultValue())),
    too_close_to_another_player(PlayerKeys.too_close_to_another_player, newProperty("Messages.To-Close-To-Another-Player", PlayerKeys.too_close_to_another_player.getDefaultValue())),
    not_a_crate(CrateKeys.not_a_crate, newProperty("Messages.Not-A-Crate", CrateKeys.not_a_crate.getDefaultValue())),
    not_a_number(CrateKeys.not_a_number, newProperty("Messages.Not-A-Number", CrateKeys.not_a_number.getDefaultValue())),
    required_keys(CrateKeys.not_enough_keys, newProperty("Messages.Required-Keys", CrateKeys.not_enough_keys.getDefaultValue())),
    not_on_block(CrateKeys.not_on_block, newProperty("Messages.Not-On-Block", CrateKeys.not_on_block.getDefaultValue())),
    out_of_time(CrateKeys.out_of_time, newProperty("Messages.Out-Of-Time", CrateKeys.out_of_time.getDefaultValue())),
    reloaded_forced_out_of_preview(CrateKeys.reloaded_forced_out_of_preview, newProperty("Messages.Forced-Out-Of-Preview", CrateKeys.reloaded_forced_out_of_preview.getDefaultValue())),
    cannot_set_type(CrateKeys.cannot_set_type, newProperty("Messages.Cannot-Set-Menu-Type", CrateKeys.cannot_set_type.getDefaultValue())),
    no_crate_permission(CrateKeys.no_crate_permission, newProperty("Messages.No-Crate-Permission", CrateKeys.no_crate_permission.getDefaultValue())),
    preview_disabled(CrateKeys.preview_disabled, newProperty("Messages.Preview-Disabled", CrateKeys.preview_disabled.getDefaultValue())),
    already_opening_crate(CrateKeys.already_opening_crate, newProperty("Messages.Already-Opening-Crate", CrateKeys.already_opening_crate.getDefaultValue())),
    crate_in_use(CrateKeys.crate_in_use, newProperty("Messages.Quick-Crate-In-Use", CrateKeys.crate_in_use.getDefaultValue())),
    cant_be_a_virtual_crate(CrateKeys.cant_be_a_virtual_crate, newProperty("Messages.Cant-Be-A-Virtual-Crate", CrateKeys.cant_be_a_virtual_crate.getDefaultValue())),
    needs_more_room(CrateKeys.needs_more_room, newProperty("Messages.Needs-More-Room", CrateKeys.needs_more_room.getDefaultValue())),
    world_disabled(CrateKeys.world_disabled, newProperty("Messages.World-Disabled", CrateKeys.world_disabled.getDefaultValue())),
    created_physical_crate(CrateKeys.created_physical_crate, newListProperty("Messages.Created-Physical-Crate", CrateKeys.created_physical_crate.getDefaultValue()), Collections.emptyList()),
    opened_a_crate(CommandKeys.opened_a_crate, newProperty("Messages.Opened-A-Crate", CommandKeys.opened_a_crate.getDefaultValue())),
    gave_a_player_keys(CommandKeys.gave_a_player_keys, newProperty("Messages.Given-A-Player-Keys", CommandKeys.gave_a_player_keys.getDefaultValue())),
    cannot_give_player_keys(CommandKeys.cannot_give_player_keys, newProperty("Messages.Cannot-Give-Player-Keys", CommandKeys.cannot_give_player_keys.getDefaultValue())),
    given_everyone_keys(CommandKeys.given_everyone_keys, newProperty("Messages.Given-Everyone-Keys", CommandKeys.given_everyone_keys.getDefaultValue())),
    given_offline_player_keys(CommandKeys.given_offline_player_keys, newProperty("Messages.Given-Offline-Player-Keys", CommandKeys.given_offline_player_keys.getDefaultValue())),
    take_player_keys(CommandKeys.take_players_keys, newProperty("Messages.Take-A-Player-Keys", CommandKeys.take_players_keys.getDefaultValue())),
    cannot_take_keys(CommandKeys.cannot_take_keys, newProperty("Messages.Cannot-Take-Keys", CommandKeys.cannot_take_keys.getDefaultValue())),
    take_offline_player_keys(CommandKeys.take_offline_player_keys, newProperty("Messages.Take-Offline-Player-Keys", CommandKeys.take_offline_player_keys.getDefaultValue())),
    no_item_in_hand(CommandKeys.no_item_in_hand, newProperty("Messages.No-Item-In-Hand", CommandKeys.no_item_in_hand.getDefaultValue())),
    added_item_with_editor(CommandKeys.added_item_with_editor, newProperty("Messages.Added-Item-With-Editor", CommandKeys.added_item_with_editor.getDefaultValue())),
    reloaded_plugin(CommandKeys.reloaded_plugin, newProperty("Messages.Reload", CommandKeys.reloaded_plugin.getDefaultValue())),
    transfer_not_enough_keys(CommandKeys.transfer_not_enough_keys, newProperty("Messages.Transfer-Keys.Not-Enough-Keys", CommandKeys.transfer_not_enough_keys.getDefaultValue())),
    transfer_sent_keys(CommandKeys.transfer_sent_keys, newProperty("Messages.Transfer-Keys.Transferred-Keys", CommandKeys.transfer_sent_keys.getDefaultValue())),
    transfer_received_keys(CommandKeys.transfer_received_keys, newProperty("Messages.Transfer-Keys.Received-Transferred-Keys", CommandKeys.transfer_received_keys.getDefaultValue())),
    personal_no_virtual_keys(CommandKeys.no_virtual_keys, newProperty("Messages.Keys.Personal.No-Virtual-Keys", CommandKeys.no_virtual_keys.getDefaultValue())),
    personal_header(CommandKeys.virtual_keys_header, newListProperty("Messages.Keys.Personal.Header", CommandKeys.virtual_keys_header.getDefaultValue()), Collections.emptyList()),
    other_no_virtual_keys(CommandKeys.other_player_no_keys, newProperty("Messages.Keys.Other-Player.No-Virtual-Keys", CommandKeys.other_player_no_keys.getDefaultValue())),
    other_header(CommandKeys.other_player_header, newListProperty("Messages.Keys.Other-Player.Header", CommandKeys.other_player_header.getDefaultValue()), Collections.emptyList()),
    per_crate(CommandKeys.per_crate, newProperty("Messages.Keys.Per-Crate", CommandKeys.per_crate.getDefaultValue())),
    help(CommandKeys.help, newListProperty("Messages.Help", CommandKeys.help.getDefaultValue()), Collections.emptyList()),
    admin_help(CommandKeys.admin_help, newListProperty("Messages.Admin-Help", CommandKeys.admin_help.getDefaultValue()), Collections.emptyList());

    private ch.jalu.configme.properties.Property<String> newString;
    private ch.jalu.configme.properties.Property<String> oldString;

    /**
     * A constructor moving the new and old string property for migration
     *
     * @param newString the new property
     * @param oldString the old property
     */
    Property(ch.jalu.configme.properties.Property<String> newString, ch.jalu.configme.properties.Property<String> oldString) {
        this.newString = newString;
        this.oldString = oldString;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveString(PropertyReader reader, ConfigurationData configuration) {
        String key = reader.getString(this.oldString.getPath());

        if (key == null) return false;

        configuration.setValue(this.newString, replace(this.oldString.determineValue(reader).getValue()));

        return true;
    }

    private ch.jalu.configme.properties.Property<Boolean> newBoolean;
    private ch.jalu.configme.properties.Property<Boolean> oldBoolean;

    /**
     * A constructor consisting of the new and old boolean property for migration
     *
     * @param newBoolean the new property
     * @param oldBoolean the old property
     * @param dummy only to differentiate from previous constructors
     */
    Property(ch.jalu.configme.properties.Property<Boolean> newBoolean, ch.jalu.configme.properties.Property<Boolean> oldBoolean, boolean dummy) {
        this.newBoolean = newBoolean;
        this.oldBoolean = oldBoolean;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveBoolean(PropertyReader reader, ConfigurationData configuration) {
        Boolean key = reader.getBoolean(this.oldBoolean.getPath());

        if (key == null) return false;

        if (this.oldBoolean.getPath().equalsIgnoreCase("root.use-old-editor")) { // invert it
            configuration.setValue(this.newBoolean, !key);

            return true;
        }

        configuration.setValue(this.newBoolean, this.oldBoolean.determineValue(reader).getValue());

        return true;
    }

    private ch.jalu.configme.properties.Property<Integer> newInteger;
    private ch.jalu.configme.properties.Property<Integer> oldInteger;

    /**
     * A constructor consisting of the new and old int property for migration
     *
     * @param newInteger the new property
     * @param oldInteger the old property
     * @param dummy only to differentiate from previous constructors
     */
    Property(ch.jalu.configme.properties.Property<Integer> newInteger, ch.jalu.configme.properties.Property<Integer> oldInteger, int dummy) {
        this.newInteger = newInteger;
        this.oldInteger = oldInteger;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveInteger(PropertyReader reader, ConfigurationData configuration) {
        Integer key = reader.getInt(this.oldInteger.getPath());

        if (key == null) return false;

        if (this.oldInteger.getPath().equalsIgnoreCase("root.gui.inventory.size") || this.oldInteger.getPath().equalsIgnoreCase("Settings.InventorySize")) { // divide it
            configuration.setValue(this.newInteger, key / 9);

            return true;
        }

        configuration.setValue(this.newInteger, this.oldInteger.determineValue(reader).getValue());

        return true;
    }

    private ch.jalu.configme.properties.Property<List<String>> newList;
    private ch.jalu.configme.properties.Property<List<String>> oldList;

    /**
     * A constructor consisting of the new and old list property for migration
     *
     * @param newList the new property
     * @param oldList the old property
     * @param dummy only to differentiate from previous constructors
     */
    Property(ch.jalu.configme.properties.Property<List<String>> newList, ch.jalu.configme.properties.Property<List<String>> oldList, List<String> dummy) {
        this.newList = newList;
        this.oldList = oldList;
    }

    /**
     * Moves the old value to the new value
     *
     * @param reader the config reader
     * @param configuration the configuration data
     * @return true or false
     */
    public boolean moveList(PropertyReader reader, ConfigurationData configuration) {
        List<?> key = reader.getList(this.oldList.getPath());

        if (key == null) return false;

        List<String> list = new ArrayList<>();

        if (this.oldList.getPath().equalsIgnoreCase("Settings.GUI-Customizer")) {
            this.oldList.determineValue(reader).getValue().forEach(line -> list.add(line.replaceAll("Item:", "item:")
                    .replaceAll("Slot:", "slot:")
                    .replaceAll("Name:", "name:")
                    .replaceAll("Lore:", "lore:")
                    .replaceAll("Glowing:", "glowing:")
                    .replaceAll("Player:", "player:")
                    .replaceAll("Unbreakable-Item", "unbreakable_item:")
                    .replaceAll("Hide-Item-Flags", "hide_item_flags:")));
        } else {
            this.oldList.determineValue(reader).getValue().forEach(line -> list.add(replace(line)));
        }

        configuration.setValue(this.newList, list);

        return true;
    }

    /**
     * Replaces old placeholders in the option when migrating.
     *
     * @param message the message to check
     * @return the finalized message to set
     */
    private String replace(String message) {
        return message.replaceAll("%page%", "{page}")
                .replaceAll("%prefix%", "{prefix}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%crate%", "{crate}")
                .replaceAll("%key%", "{key}")
                .replaceAll("%keys%", "{keys}")
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