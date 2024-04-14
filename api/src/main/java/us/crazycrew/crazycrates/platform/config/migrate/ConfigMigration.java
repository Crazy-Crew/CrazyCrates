package us.crazycrew.crazycrates.platform.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.Properties;

@ApiStatus.Internal
public class ConfigMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateLocale(reader, configurationData);
    }

    private boolean migrateLocale(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return Properties.enable_crate_menu.moveBoolean(reader, configurationData)
                | Properties.show_quickcrate.moveBoolean(reader, configurationData)
                | Properties.knockback.moveBoolean(reader, configurationData)

                | Properties.force_out_of_preview.moveBoolean(reader, configurationData)
                | Properties.send_preview_message.moveBoolean(reader, configurationData)

                | Properties.cosmic_crate_timeout.moveBoolean(reader, configurationData)

                | Properties.physical_accepts_virtual_keys.moveBoolean(reader, configurationData)
                | Properties.physical_accepts_physical_keys.moveBoolean(reader, configurationData)
                | Properties.virtual_accepts_physical_keys.moveBoolean(reader, configurationData)

                | Properties.give_virtual_keys_when_inventory_full.moveBoolean(reader, configurationData)
                | Properties.give_virtual_keys_when_inventory_full_message.moveBoolean(reader, configurationData)

                | Properties.quadcrate_timer.moveInteger(reader, configurationData)

                | Properties.need_key_sound_toggle.moveBoolean(reader, configurationData)
                | Properties.need_key_sound.moveString(reader, configurationData)

                | Properties.inventory_name.moveString(reader, configurationData)
                | Properties.inventory_size.moveInteger(reader, configurationData)

                | Properties.disabled_worlds.moveList(reader, configurationData)

                | Properties.menu_button_override.moveBoolean(reader, configurationData)
                | Properties.menu_button_commands.moveList(reader, configurationData)
                | Properties.menu_button_name.moveString(reader, configurationData)
                | Properties.menu_button_item.moveString(reader, configurationData)
                | Properties.menu_button_lore.moveList(reader, configurationData)

                | Properties.next_button_name.moveString(reader, configurationData)
                | Properties.next_button_item.moveString(reader, configurationData)
                | Properties.next_button_lore.moveList(reader, configurationData)

                | Properties.back_button_name.moveString(reader, configurationData)
                | Properties.back_button_item.moveString(reader, configurationData)
                | Properties.back_button_lore.moveList(reader, configurationData)

                | Properties.filler_toggle.moveBoolean(reader, configurationData)
                | Properties.filler_item.moveString(reader, configurationData)
                | Properties.filler_name.moveString(reader, configurationData)
                | Properties.filler_lore.moveList(reader, configurationData)

                | Properties.gui_customizer_toggle.moveBoolean(reader, configurationData)
                | Properties.gui_customizer_lore.moveList(reader, configurationData);
    }
}