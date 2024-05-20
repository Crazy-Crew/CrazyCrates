package com.badbones69.crazycrates.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.FileProperty;

public class ConfigMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateLocale(reader, configurationData);
    }

    private boolean migrateLocale(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return FileProperty.enable_crate_menu.moveBoolean(reader, configurationData)
                | FileProperty.show_quickcrate.moveBoolean(reader, configurationData)
                | FileProperty.knockback.moveBoolean(reader, configurationData)

                | FileProperty.force_out_of_preview.moveBoolean(reader, configurationData)
                | FileProperty.send_preview_message.moveBoolean(reader, configurationData)

                | FileProperty.cosmic_crate_timeout.moveBoolean(reader, configurationData)

                | FileProperty.physical_accepts_virtual_keys.moveBoolean(reader, configurationData)
                | FileProperty.physical_accepts_physical_keys.moveBoolean(reader, configurationData)
                | FileProperty.virtual_accepts_physical_keys.moveBoolean(reader, configurationData)

                | FileProperty.give_virtual_keys_when_inventory_full.moveBoolean(reader, configurationData)
                | FileProperty.give_virtual_keys_when_inventory_full_message.moveBoolean(reader, configurationData)

                | FileProperty.quadcrate_timer.moveInteger(reader, configurationData)

                | FileProperty.need_key_sound_toggle.moveBoolean(reader, configurationData)
                | FileProperty.need_key_sound.moveString(reader, configurationData)

                | FileProperty.inventory_name.moveString(reader, configurationData)
                | FileProperty.inventory_size.moveInteger(reader, configurationData)

                | FileProperty.disabled_worlds.moveList(reader, configurationData)

                | FileProperty.menu_button_override.moveBoolean(reader, configurationData)
                | FileProperty.menu_button_commands.moveList(reader, configurationData)
                | FileProperty.menu_button_name.moveString(reader, configurationData)
                | FileProperty.menu_button_item.moveString(reader, configurationData)
                | FileProperty.menu_button_lore.moveList(reader, configurationData)

                | FileProperty.next_button_name.moveString(reader, configurationData)
                | FileProperty.next_button_item.moveString(reader, configurationData)
                | FileProperty.next_button_lore.moveList(reader, configurationData)

                | FileProperty.back_button_name.moveString(reader, configurationData)
                | FileProperty.back_button_item.moveString(reader, configurationData)
                | FileProperty.back_button_lore.moveList(reader, configurationData)

                | FileProperty.filler_toggle.moveBoolean(reader, configurationData)
                | FileProperty.filler_item.moveString(reader, configurationData)
                | FileProperty.filler_name.moveString(reader, configurationData)
                | FileProperty.filler_lore.moveList(reader, configurationData)

                | FileProperty.gui_customizer_toggle.moveBoolean(reader, configurationData)
                | FileProperty.gui_customizer_lore.moveList(reader, configurationData);
    }
}