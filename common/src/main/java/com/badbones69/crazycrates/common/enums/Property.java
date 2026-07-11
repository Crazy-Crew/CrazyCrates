package com.badbones69.crazycrates.common.enums;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    gui_customizer_lore(ConfigKeys.gui_customizer, newListProperty("Settings.GUI-Customizer", ConfigKeys.gui_customizer.getDefaultValue()), Collections.emptyList());

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

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
        final List<?> key = reader.getList(this.oldList.getPath());

        if (key == null) return false;

        final List<String> list = new ArrayList<>();

        if (this.oldList.getPath().equalsIgnoreCase("Settings.GUI-Customizer")) {
            final List<String> lines = this.oldList.determineValue(reader).getValue();

            for (final String line : lines) {
                if (line.isBlank()) continue;

                list.add(this.fusion.replacePlaceholders(
                        line,
                        Map.of(
                                "Item:", "item:",
                                "Slot:", "slot:",
                                "Name:", "name:",
                                "Lore:", "lore:",
                                "Glowing:", "glowing:",
                                "Player:", "player:",
                                "Unbreakable-Item", "unbreakable_item:",
                                "Hide-Item-Flags", "hide_item_flags:"
                        )
                ));
            }
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
    private String replace(@NotNull final String message) {
        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%page%", "{page}");
        placeholders.put("%prefix%", "{prefix}");
        placeholders.put("%world%", "{world}");
        placeholders.put("%crate%", "{crate}");
        placeholders.put("%key%", "{key}");
        placeholders.put("%keys%", "{keys}");
        placeholders.put("%cratetype%", "{cratetype}");
        placeholders.put("%player%", "{player}");
        placeholders.put("%prize%", "{prize}");
        placeholders.put("%number%", "{number}");
        placeholders.put("%keytype%", "{keytype}");
        placeholders.put("%usage%", "{usage}");
        placeholders.put("%key-amount%", "{amount}");
        placeholders.put("%amount%", "{amount}");
        placeholders.put("%id%", "{id}");

        return this.fusion.replacePlaceholders(message, placeholders);
    }
}