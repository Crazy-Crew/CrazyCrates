package com.badbones69.crazycrates.config.migrate;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.enums.FileProperty;

public class LocaleMigration extends PlainMigrationService {

    @Override
    protected boolean performMigrations(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return migrateConfig(reader, configurationData);
    }

    private boolean migrateConfig(@NotNull PropertyReader reader, @NotNull ConfigurationData configurationData) {
        return FileProperty.unknown_command.moveString(reader, configurationData)
                | FileProperty.no_teleporting.moveString(reader, configurationData)
                | FileProperty.no_commands_while_in_crate.moveString(reader, configurationData)
                | FileProperty.no_key.moveString(reader, configurationData)
                | FileProperty.no_virtual_key.moveString(reader, configurationData)
                | FileProperty.correct_usage.moveString(reader, configurationData)
                | FileProperty.feature_disabled.moveString(reader, configurationData)
                | FileProperty.no_prizes_found.moveString(reader, configurationData)
                | FileProperty.no_schematics_found.moveString(reader, configurationData)
                | FileProperty.internal_error.moveString(reader, configurationData)
                | FileProperty.inventory_full.moveString(reader, configurationData)
                | FileProperty.prize_error.moveString(reader, configurationData)
                | FileProperty.must_be_player.moveString(reader, configurationData)
                | FileProperty.must_be_console.moveString(reader, configurationData)
                | FileProperty.must_be_looking_at_block.moveString(reader, configurationData)
                | FileProperty.not_online.moveString(reader, configurationData)
                | FileProperty.same_player.moveString(reader, configurationData)
                | FileProperty.no_permission.moveString(reader, configurationData)
                | FileProperty.obtaining_keys.moveString(reader, configurationData)
                | FileProperty.too_close_to_another_player.moveString(reader, configurationData)
                | FileProperty.not_a_crate.moveString(reader, configurationData)
                | FileProperty.not_a_number.moveString(reader, configurationData)
                | FileProperty.required_keys.moveString(reader, configurationData)
                | FileProperty.not_on_block.moveString(reader, configurationData)
                | FileProperty.out_of_time.moveString(reader, configurationData)
                | FileProperty.reloaded_forced_out_of_preview.moveString(reader, configurationData)
                | FileProperty.cannot_set_type.moveString(reader, configurationData)
                | FileProperty.no_crate_permission.moveString(reader, configurationData)
                | FileProperty.preview_disabled.moveString(reader, configurationData)
                | FileProperty.already_opening_crate.moveString(reader, configurationData)
                | FileProperty.crate_in_use.moveString(reader, configurationData)
                | FileProperty.cant_be_a_virtual_crate.moveString(reader, configurationData)
                | FileProperty.needs_more_room.moveString(reader, configurationData)
                | FileProperty.world_disabled.moveString(reader, configurationData)
                | FileProperty.created_physical_crate.moveList(reader, configurationData)
                | FileProperty.opened_a_crate.moveString(reader, configurationData)
                | FileProperty.gave_a_player_keys.moveString(reader, configurationData)
                | FileProperty.cannot_give_player_keys.moveString(reader, configurationData)
                | FileProperty.given_everyone_keys.moveString(reader, configurationData)
                | FileProperty.given_offline_player_keys.moveString(reader, configurationData)
                | FileProperty.take_player_keys.moveString(reader, configurationData)
                | FileProperty.cannot_take_keys.moveString(reader, configurationData)
                | FileProperty.take_offline_player_keys.moveString(reader, configurationData)
                | FileProperty.no_item_in_hand.moveString(reader, configurationData)
                | FileProperty.added_item_with_editor.moveString(reader, configurationData)
                | FileProperty.reloaded_plugin.moveString(reader, configurationData)
                | FileProperty.transfer_not_enough_keys.moveString(reader, configurationData)
                | FileProperty.transfer_sent_keys.moveString(reader, configurationData)
                | FileProperty.transfer_received_keys.moveString(reader, configurationData)
                | FileProperty.personal_no_virtual_keys.moveString(reader, configurationData)
                | FileProperty.personal_header.moveList(reader, configurationData)
                | FileProperty.other_no_virtual_keys.moveString(reader, configurationData)
                | FileProperty.other_header.moveList(reader, configurationData)
                | FileProperty.per_crate.moveString(reader, configurationData)
                | FileProperty.help.moveList(reader, configurationData)
                | FileProperty.admin_help.moveList(reader, configurationData);
    }
}