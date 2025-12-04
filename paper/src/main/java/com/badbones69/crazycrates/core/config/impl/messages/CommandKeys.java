package com.badbones69.crazycrates.core.config.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class CommandKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to commands."
        };

        conf.setComment("command", header);
    }

    @Comment("A list of available placeholders: {crate}, {player}")
    public static final Property<String> opened_a_crate = newProperty("command.open.opened-a-crate", "{prefix}<gray>You have opened the {crate} <gray>for <gold>{player}.");

    @Comment("A list of available placeholders: {amount}, {player}, {keytype}, {key}")
    public static final Property<String> gave_a_player_keys = newProperty("command.give.given-player-keys", "{prefix}<gray>You have given <gold>{player} {amount} <gray>key(s).");

    @Comment("A list of available placeholders: {amount}, {amount}, {keytype}, {key}")
    public static final Property<String> cannot_give_player_keys = newProperty("command.give.full-inventory", "{prefix}<gray>You have been given <gold>{amount} {key} <gray>virtual key(s) because your inventory was full.");

    @Comment("A list of available placeholders: {amount}, {keytype}, {key}")
    public static final Property<String> given_everyone_keys = newProperty("command.give.given-everyone-keys", "{prefix}<gray>You have given everyone <gold>{amount} <gray>key(s).");

    @Comment("A list of available placeholders: {amount}, {player}, {keytype}")
    public static final Property<String> given_offline_player_keys = newProperty("command.give.given-offline-player-keys", "{prefix}<gray>You have given <gold>{amount} <gray>key(s) to the offline player <gold>{player}.");

    @Comment("A list of available placeholders: {amount}, {player}, {keytype}")
    public static final Property<String> take_players_keys = newProperty("command.take.take-player-keys", "{prefix}<gray>You have taken <gold>{amount} <gray>key(s) from <gold>{player}.");

    @Comment("A list of available placeholders: {player}")
    public static final Property<String> cannot_take_keys = newProperty("command.take.cannot-take-keys", "{prefix}<gray>You cannot take key(s) from <gold>{player} <gray>as they are poor.");

    @Comment("A list of available placeholders: {amount}, {player}, {keytype}")
    public static final Property<String> take_offline_player_keys = newProperty("command.take.take-offline-player-keys", "{prefix}<gray>You have taken <gold>{amount} <gray>key(s) from the offline player <gold>{player}.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> no_item_in_hand = newProperty("command.additem.no-item-in-hand", "{prefix}<red>You need to have an item in your hand to add it {crate}.");

    @Comment("A list of available placeholders: {crate}, {prize}")
    public static final Property<String> added_item_with_editor = newProperty("command.additem.add-item-from-hand", "{prefix}<gray>The item has been added to the {crate} in prize #{prize}.");

    @Comment("A list of available placeholders: {file}, {type}, {reason}")
    public static final Property<String> error_migrating = newProperty("command.migrate.error", "{prefix}<red>We could not migrate <green>{file} <red>using <green>{type} <red>migration for <green>{reason}.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> migration_not_available = newProperty("command.migrate.not-available", "{prefix}<green>This migration type is not available.");

    @Comment("A list of available placeholders: {name}")
    public static final Property<String> migration_plugin_not_enabled = newProperty("command.migrate.plugin-not-available", "{prefix}<green>The plugin <red>{name} <green>is not enabled. Cannot use as migration!");

    public static final Property<String> migration_no_crates_available = newProperty("command.migrate.no-crates-available", "{prefix}<green>There is no crates available for migration!");

    public static final Property<List<String>> successfully_migrated_users = newListProperty("command.migrate.success-users", List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Migration Stats ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Successful Conversions: ",
            " ⤷ {succeeded_amount}</green>",
            "<dark_gray>»</dark_gray> <red>Failed Conversions: ",
            " ⤷ {failed_amount}</red>",
            "",
            "<red>Conversion Time: <yellow>{time}",
            "<red>Conversion Type: <yellow>{type}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    ));

    @Comment({
            "A list of available placeholders: {type}, {files}",
            "",
            "{files} will output multiple crates if migrating from another plugin"
    })
    public static final Property<List<String>> successfully_migrated = newListProperty("command.migrate.success", List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Migration Stats ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Successful Conversions: ",
            " ⤷ {succeeded_amount}</green>",
            "<dark_gray>»</dark_gray> <red>Failed Conversions: ",
            " ⤷ {failed_amount}</red>",
            "",
            "<red>Conversion Time: <yellow>{time}",
            "<red>Conversion Type: <yellow>{type}",
            "",
            "<red>Converted Files:",
            "{files}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    ));

    public static final Property<String> reloaded_plugin = newProperty("command.reload.completed", "{prefix}<dark_aqua>You have reloaded the Config and Data Files.");

    public static final Property<String> transfer_not_enough_keys = newProperty("command.transfer.not-enough-keys", "{prefix}<red>You do not have enough keys to transfer.");

    @Comment("A list of available placeholders: {amount}, {player}, {keytype}, {crate}")
    public static final Property<String> transfer_sent_keys = newProperty("command.transfer.transferred-keys", "{prefix}<gray>You have transferred {amount} {crate} keys to {player}.");

    @Comment("A list of available placeholders: {amount}, {player}, {keytype}, {crate}")
    public static final Property<String> transfer_received_keys = newProperty("command.transfer.transferred-keys-received", "{prefix}<gray>You have received {amount} {crate} keys from {player}.");

    public static final Property<String> no_virtual_keys = newProperty("command.keys.personal.no-virtual-keys", "{prefix}<bold><dark_gray>(<dark_red>!<dark_gray>)</bold> <gray>You currently do not have any virtual keys.");

    @Comment("A list of available placeholders: {crates_opened}")
    public static final Property<List<String>> virtual_keys_header = newListProperty("command.keys.personal.virtual-keys-header", List.of(
            "<bold><dark_gray>(<gold>!<dark_gray>)</bold> <gray>List of your current number of keys.",
            " <yellow> -> Total Crates Opened: <red>{crates_opened}",
            ""
    ));

    @Comment("A list of available placeholders: {player}")
    public static final Property<String> other_player_no_keys = newProperty("command.keys.other-player.no-virtual-keys", "{prefix}<bold><dark_gray>(<dark_red>!<dark_gray>)</bold> <gray>The player {player} does not have any keys.");

    @Comment("A list of available placeholders: {player}, {crates_opened}")
    public static final Property<List<String>> other_player_header = newListProperty("command.keys.other-player.virtual-keys-header", List.of(
            "<bold><dark_gray>(<gold>!<dark_gray>)</bold> <gray>List of {player}''s current number of keys.",
            " <yellow> -> Total Crates Opened: <red>{crates_opened}",
            ""
    ));

    @Comment("A list of available placeholders: {crate}, {keys}, {crate_opened}")
    public static final Property<String> per_crate = newProperty("command.keys.crate-format", "{crate} <bold><gray>><dark_gray>></bold> <gold>{keys} keys <gray>: Opened <gold>{crate_opened} times");

    @Comment("This requires crazycrates.command.help")
    public static final Property<List<String>> help = newListProperty("command.player-help", List.of(
            "<bold><yellow>Crazy Crates Player Help</bold>",
            "",
            "<gold>/keys view [player] <gray>- <yellow>Check the number of keys a player has.",
            "<gold>/keys <gray>- <yellow>Shows how many keys you have.",
            "<gold>/crates <gray>- <yellow>Opens the menu."
    ));

    @Comment("This requires crazycrates.command.admin.help")
    public static final Property<List<String>> admin_help = newListProperty("command.admin-help", List.of(
            "<bold><red>Crazy Crates Admin Help</bold>",
            "",
            "<gold>/crates additem <crate_name> <prize_number> <chance> [tier] <gray>- <yellow>Add items in-game to a prize in a crate including Cosmic/Casino.",
            "<gold>/crates preview <crate_name> [player] <gray>- <yellow>Opens the preview of a crate for a player.",
            "<gold>/crates list <gray>- <yellow>Lists all crates.",
            "<gold>/crates open <crate_name> <gray>- <yellow>Tries to open a crate for you if you have a key.",
            "<gold>/crates open-others <crate_name> [player] <gray>- <yellow>Tries to open a crate for a player if they have a key.",
            "<gold>/crates transfer <crate_name> [player] [amount <gray>- <yellow>Transfers keys to players you chose.",
            "<gold>/crates debug <gray>- <yellow>Debugs crates",
            "<gold>/crates admin <gray>- <yellow>Shows admin menu",
            "<gold>/crates forceopen <crate_name> [player] <gray>- <yellow>Opens a crate for a player for free.",
            "<gold>/crates mass-open <crate_name> <physical/virtual> [amount] <gray>- <yellow>Mass opens a set amount of crates.",
            "<gold>/crates tp <location> <gray>- <yellow>Teleport to a Crate.",
            "<gold>/crates give <physical/virtual> <crate_name> [amount] [player] [-s/--silent] <gray>- <yellow>Allows you to take keys from a player.",
            "<gold>/crates give-random <physical/virtual> <amount> [-s/--silent] <gray>- <yellow>Gives a player random crate keys.",
            "<gold>/crates give-all <physical/virtual> <crate> <amount> [-s/--silent] <gray>- <yellow>Gives all online players keys to use on a crate.",
            "<gold>/crates editor -c/--crate <crate_name> or -e/--exit <gray>- <yellow>Allows you to enter/exit editor mode.",
            "<gold>/crates migrate -mt/--migration_type <migration_type> --crate/-crate <crate> --data/-d <gray>- <yellow>A command to migrate from other plugins or for internal changes.",
            "<gold>/crates reload <gray>- <yellow>Reloads the config/data files.",
            "<gold>/crates set1/set2 <gray>- <yellow>Sets position <red>#1 <yellow>or <red>#2 <yellow>for when making a new schematic for QuadCrates.",
            "<gold>/crates save <file name> <gray>- <yellow>Create a new nbt file in the schematics folder.",
            "",
            "<gold>/keys view [player] <gray>- <yellow>Check the number of keys a player has.",
            "<gold>/keys <gray>- <yellow>Shows how many keys you have.",
            "<gold>/crates <gray>- <yellow>Opens the menu.",
            "",
            "<gray>You can find a list of permissions @ <yellow>https://docs.crazycrew.us/docs/plugins/crazycrates/commands/permissions"
    ));
}