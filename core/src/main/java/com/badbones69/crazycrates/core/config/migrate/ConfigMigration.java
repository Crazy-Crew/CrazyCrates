package com.badbones69.crazycrates.core.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazycrates.core.enums.Property;
import org.jetbrains.annotations.NotNull;

public class ConfigMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateLocale(reader, configurationData);
    }

    private boolean migrateLocale(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return Property.enable_crate_menu.moveBoolean(reader, configurationData)
                | Property.show_quickcrate.moveBoolean(reader, configurationData)
                | Property.knockback.moveBoolean(reader, configurationData)

                | Property.force_out_of_preview.moveBoolean(reader, configurationData)
                | Property.send_preview_message.moveBoolean(reader, configurationData)

                | Property.cosmic_crate_timeout.moveBoolean(reader, configurationData)

                | Property.physical_accepts_virtual_keys.moveBoolean(reader, configurationData)
                | Property.physical_accepts_physical_keys.moveBoolean(reader, configurationData)
                | Property.virtual_accepts_physical_keys.moveBoolean(reader, configurationData)

                | Property.give_virtual_keys_when_inventory_full.moveBoolean(reader, configurationData)
                | Property.give_virtual_keys_when_inventory_full_message.moveBoolean(reader, configurationData)

                | Property.quadcrate_timer.moveInteger(reader, configurationData)

                | Property.need_key_sound_toggle.moveBoolean(reader, configurationData)
                | Property.need_key_sound.moveString(reader, configurationData)

                | Property.inventory_name.moveString(reader, configurationData)
                | Property.inventory_size.moveInteger(reader, configurationData)

                | Property.disabled_worlds.moveList(reader, configurationData)

                | Property.menu_button_override.moveBoolean(reader, configurationData)
                | Property.menu_button_commands.moveList(reader, configurationData)
                | Property.menu_button_name.moveString(reader, configurationData)
                | Property.menu_button_item.moveString(reader, configurationData)
                | Property.menu_button_lore.moveList(reader, configurationData)

                | Property.next_button_name.moveString(reader, configurationData)
                | Property.next_button_item.moveString(reader, configurationData)
                | Property.next_button_lore.moveList(reader, configurationData)

                | Property.back_button_name.moveString(reader, configurationData)
                | Property.back_button_item.moveString(reader, configurationData)
                | Property.back_button_lore.moveList(reader, configurationData)

                | Property.filler_toggle.moveBoolean(reader, configurationData)
                | Property.filler_item.moveString(reader, configurationData)
                | Property.filler_name.moveString(reader, configurationData)
                | Property.filler_lore.moveList(reader, configurationData)

                | Property.gui_customizer_toggle.moveBoolean(reader, configurationData)
                | Property.gui_customizer_lore.moveList(reader, configurationData);
    }
}