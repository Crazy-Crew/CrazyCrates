package com.badbones69.crazycrates.core.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import com.badbones69.crazycrates.core.enums.Property;
import org.jetbrains.annotations.NotNull;

public class LocaleMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateConfig(reader, configurationData);
    }

    private boolean migrateConfig(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return Property.unknown_command.moveString(reader, configurationData)
                | Property.no_teleporting.moveString(reader, configurationData)
                | Property.no_commands_while_in_crate.moveString(reader, configurationData)
                | Property.no_key.moveString(reader, configurationData)
                | Property.no_virtual_key.moveString(reader, configurationData)
                | Property.correct_usage.moveString(reader, configurationData)
                | Property.feature_disabled.moveString(reader, configurationData)
                | Property.no_prizes_found.moveString(reader, configurationData)
                | Property.no_schematics_found.moveString(reader, configurationData)
                | Property.internal_error.moveString(reader, configurationData)
                | Property.inventory_full.moveString(reader, configurationData)
                | Property.prize_error.moveString(reader, configurationData)
                | Property.must_be_player.moveString(reader, configurationData)
                | Property.must_be_console.moveString(reader, configurationData)
                | Property.must_be_looking_at_block.moveString(reader, configurationData)
                | Property.not_online.moveString(reader, configurationData)
                | Property.same_player.moveString(reader, configurationData)
                | Property.no_permission.moveString(reader, configurationData)
                | Property.obtaining_keys.moveString(reader, configurationData)
                | Property.too_close_to_another_player.moveString(reader, configurationData)
                | Property.not_a_crate.moveString(reader, configurationData)
                | Property.not_a_number.moveString(reader, configurationData)
                | Property.required_keys.moveString(reader, configurationData)
                | Property.not_on_block.moveString(reader, configurationData)
                | Property.out_of_time.moveString(reader, configurationData)
                | Property.reloaded_forced_out_of_preview.moveString(reader, configurationData)
                | Property.cannot_set_type.moveString(reader, configurationData)
                | Property.no_crate_permission.moveString(reader, configurationData)
                | Property.preview_disabled.moveString(reader, configurationData)
                | Property.already_opening_crate.moveString(reader, configurationData)
                | Property.crate_in_use.moveString(reader, configurationData)
                | Property.cant_be_a_virtual_crate.moveString(reader, configurationData)
                | Property.needs_more_room.moveString(reader, configurationData)
                | Property.world_disabled.moveString(reader, configurationData)
                | Property.created_physical_crate.moveList(reader, configurationData)
                | Property.opened_a_crate.moveString(reader, configurationData)
                | Property.gave_a_player_keys.moveString(reader, configurationData)
                | Property.cannot_give_player_keys.moveString(reader, configurationData)
                | Property.given_everyone_keys.moveString(reader, configurationData)
                | Property.given_offline_player_keys.moveString(reader, configurationData)
                | Property.take_player_keys.moveString(reader, configurationData)
                | Property.cannot_take_keys.moveString(reader, configurationData)
                | Property.take_offline_player_keys.moveString(reader, configurationData)
                | Property.no_item_in_hand.moveString(reader, configurationData)
                | Property.added_item_with_editor.moveString(reader, configurationData)
                | Property.reloaded_plugin.moveString(reader, configurationData)
                | Property.transfer_not_enough_keys.moveString(reader, configurationData)
                | Property.transfer_sent_keys.moveString(reader, configurationData)
                | Property.transfer_received_keys.moveString(reader, configurationData)
                | Property.personal_no_virtual_keys.moveString(reader, configurationData)
                | Property.personal_header.moveList(reader, configurationData)
                | Property.other_no_virtual_keys.moveString(reader, configurationData)
                | Property.other_header.moveList(reader, configurationData)
                | Property.per_crate.moveString(reader, configurationData)
                | Property.help.moveList(reader, configurationData)
                | Property.admin_help.moveList(reader, configurationData);
    }
}