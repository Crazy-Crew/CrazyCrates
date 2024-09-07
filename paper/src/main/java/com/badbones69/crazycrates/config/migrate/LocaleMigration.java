package com.badbones69.crazycrates.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.Properties;

public class LocaleMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateConfig(reader, configurationData);
    }

    private boolean migrateConfig(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return Properties.unknown_command.moveString(reader, configurationData)
                | Properties.no_teleporting.moveString(reader, configurationData)
                | Properties.no_commands_while_in_crate.moveString(reader, configurationData)
                | Properties.no_key.moveString(reader, configurationData)
                | Properties.no_virtual_key.moveString(reader, configurationData)
                | Properties.correct_usage.moveString(reader, configurationData)
                | Properties.feature_disabled.moveString(reader, configurationData)
                | Properties.no_prizes_found.moveString(reader, configurationData)
                | Properties.no_schematics_found.moveString(reader, configurationData)
                | Properties.internal_error.moveString(reader, configurationData)
                | Properties.inventory_full.moveString(reader, configurationData)
                | Properties.prize_error.moveString(reader, configurationData)
                | Properties.must_be_player.moveString(reader, configurationData)
                | Properties.must_be_console.moveString(reader, configurationData)
                | Properties.must_be_looking_at_block.moveString(reader, configurationData)
                | Properties.not_online.moveString(reader, configurationData)
                | Properties.same_player.moveString(reader, configurationData)
                | Properties.no_permission.moveString(reader, configurationData)
                | Properties.obtaining_keys.moveString(reader, configurationData)
                | Properties.too_close_to_another_player.moveString(reader, configurationData)
                | Properties.not_a_crate.moveString(reader, configurationData)
                | Properties.not_a_number.moveString(reader, configurationData)
                | Properties.required_keys.moveString(reader, configurationData)
                | Properties.not_on_block.moveString(reader, configurationData)
                | Properties.out_of_time.moveString(reader, configurationData)
                | Properties.reloaded_forced_out_of_preview.moveString(reader, configurationData)
                | Properties.cannot_set_type.moveString(reader, configurationData)
                | Properties.no_crate_permission.moveString(reader, configurationData)
                | Properties.preview_disabled.moveString(reader, configurationData)
                | Properties.already_opening_crate.moveString(reader, configurationData)
                | Properties.crate_in_use.moveString(reader, configurationData)
                | Properties.cant_be_a_virtual_crate.moveString(reader, configurationData)
                | Properties.needs_more_room.moveString(reader, configurationData)
                | Properties.world_disabled.moveString(reader, configurationData)
                | Properties.created_physical_crate.moveList(reader, configurationData)
                | Properties.opened_a_crate.moveString(reader, configurationData)
                | Properties.gave_a_player_keys.moveString(reader, configurationData)
                | Properties.cannot_give_player_keys.moveString(reader, configurationData)
                | Properties.given_everyone_keys.moveString(reader, configurationData)
                | Properties.given_offline_player_keys.moveString(reader, configurationData)
                | Properties.take_player_keys.moveString(reader, configurationData)
                | Properties.cannot_take_keys.moveString(reader, configurationData)
                | Properties.take_offline_player_keys.moveString(reader, configurationData)
                | Properties.no_item_in_hand.moveString(reader, configurationData)
                | Properties.added_item_with_editor.moveString(reader, configurationData)
                | Properties.reloaded_plugin.moveString(reader, configurationData)
                | Properties.transfer_not_enough_keys.moveString(reader, configurationData)
                | Properties.transfer_sent_keys.moveString(reader, configurationData)
                | Properties.transfer_received_keys.moveString(reader, configurationData)
                | Properties.personal_no_virtual_keys.moveString(reader, configurationData)
                | Properties.personal_header.moveList(reader, configurationData)
                | Properties.other_no_virtual_keys.moveString(reader, configurationData)
                | Properties.other_header.moveList(reader, configurationData)
                | Properties.per_crate.moveString(reader, configurationData)
                | Properties.help.moveList(reader, configurationData)
                | Properties.admin_help.moveList(reader, configurationData);
    }
}