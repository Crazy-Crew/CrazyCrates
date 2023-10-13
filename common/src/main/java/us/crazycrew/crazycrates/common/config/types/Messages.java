package us.crazycrew.crazycrates.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import java.util.List;

public class Messages implements SettingsHolder {

    protected Messages() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "",
                "Docs: https://docs.crazycrew.us/crazycrates/home"
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };

        conf.setComment("misc", header);
    }

    public static final Property<String> unknown_command = PropertyInitializer.newProperty("misc.unknown-command", "<red>This command is not known.");

    public static final Property<String> no_teleporting = PropertyInitializer.newProperty("misc.no-teleporting", "<red>You may not teleport away while opening <yellow>{crate}.");

    public static final Property<String> no_commands = PropertyInitializer.newProperty("misc.no-commands", "<red>You are not allowed to use commands while opening <yellow>{crate}.");

    public static final Property<String> no_keys = PropertyInitializer.newProperty("misc.no-keys", "<red>You need a <reset>{key} <red>in your hand to use <yellow>{crate}.");

    public static final Property<String> no_virtual_keys = PropertyInitializer.newProperty("misc.no-virtual-keys", "<red>You need <reset>{key} <red>to open <yellow>{crate}.");

    public static final Property<String> feature_disabled = PropertyInitializer.newProperty("misc.feature-disabled", "<red>This feature is disabled. We have no ETA on when this will function.");

    public static final Property<String> correct_usage = PropertyInitializer.newProperty("misc.correct-usage", "<red>The correct usage for this command is <gold>{usage}");

    // Errors
    public static final Property<String> no_prizes_found = PropertyInitializer.newProperty("errors.no-prizes-found", "<red>This crate contains no prizes that you can win.");

    public static final Property<String> key_refund = PropertyInitializer.newProperty("errors.key-refund", "<red>There was an error with prize <yellow>{crate} <red>so you've been refunded your key");

    public static final Property<String> no_schematics_found = PropertyInitializer.newProperty("errors.no-schematics-found", "<red>No schematic were found, Please re-generate them by deleting the folder or checking for errors!");

    public static final Property<List<String>> prize_error = PropertyInitializer.newListProperty("errors.prize-error", List.of(
            "<red>An error has occurred while trying to give you the prize <yellow>{prize}.",
            "<gold>This has occurred in <yellow>{crate}. <gold>Please notify your owner."
    ));

    public static final Property<String> internal_error = PropertyInitializer.newProperty("errors.internal-error", "<red>An internal error has occurred. Please check the console for the full error.");

    // Players
    public static final Property<String> player_requirements_must_be_player = PropertyInitializer.newProperty("player.requirements.must-be-player", "<red>You must be a player to use this command.");

    public static final Property<String> player_requirements_must_be_console_sender = PropertyInitializer.newProperty("player.requirements.must-be-console-sender", "<red>You must be using console to use this command.");

    public static final Property<String> player_requirements_must_be_looking_at_block = PropertyInitializer.newProperty("player.requirements.must-be-looking-at-block", "<red>You must be looking at a block.");

    public static final Property<String> player_requirements_target_not_online = PropertyInitializer.newProperty("player.target-not-online", "<red>The player <yellow>{player} <red>is not online.");

    public static final Property<String> player_requirements_same_player = PropertyInitializer.newProperty("player.target-same-player", "<red>You cannot use this command on yourself.");

    public static final Property<String> player_requirements_no_permission = PropertyInitializer.newProperty("player.no-permission", "<red>You do not have permission to use that command!");

    public static final Property<String> player_requirements_inventory_not_empty = PropertyInitializer.newProperty("player.inventory-not-empty", "<red>Inventory is not empty, Please make room before opening <yellow>{crate}.");

    public static final Property<String> player_requirements_obtaining_keys = PropertyInitializer.newProperty("player.obtaining-keys", "<gray>You have been given <yellow>{amount} {key} <gray>Keys.");

    public static final Property<String> player_requirements_too_close_to_another_player = PropertyInitializer.newProperty("player.too-close-to-another-player", "<red>You are too close to a player that is opening their Crate.");

    public static final Property<String> player_requirements_required_keys = PropertyInitializer.newProperty("player.required-keys", "<gray>You need <red>{key-amount} <gray>keys to open <red>{crate}. <gray>You have <red>{amount}.");

    // Crates
    public static final Property<String> crate_requirements_not_a_crate = PropertyInitializer.newProperty("crates.requirements.not-a-crate", "<red>There is no crate called <yellow>{crate}.");

    public static final Property<String> crate_requirements_no_permission = PropertyInitializer.newProperty("crates.requirements.no-permission", "<red>You do not have permission to use that crate");

    public static final Property<String> crate_requirements_not_a_number = PropertyInitializer.newProperty("crates.requirements.not-a-number", "<yellow>{number} <red>is not a number.");
    public static final Property<String> crate_requirements_not_on_block = PropertyInitializer.newProperty("crates.requirements.not-on-block", "<red>You must be standing on a block to use <yellow>{crate}.");

    public static final Property<String> crate_requirements_out_of_time = PropertyInitializer.newProperty("crates.out-of-time", "<red>You took <green>5 Minutes <red>to open the <yellow>{crate} <red>so it closed.");

    public static final Property<String> crate_requirements_preview_disabled = PropertyInitializer.newProperty("crates.crate-preview-disabled", "<red>The preview for <yellow>{crate} <gray>is currently disabled.");

    public static final Property<String> crate_requirements_already_open = PropertyInitializer.newProperty("crates.crate-already-open", "<red>You are already opening <yellow>{crate}.");

    public static final Property<String> crate_requirements_in_use = PropertyInitializer.newProperty("crates.crate-in-use", "<yellow>{crate} <red>is already in use. Please wait until it finishes!");

    public static final Property<String> crate_requirements_cannot_be_a_virtual_crate = PropertyInitializer.newProperty("crates.cannot-be-a-virtual-crate", "<yellow>{crate} <red>cannot be used as a Virtual Crate. You have it set to <yellow>{cratetype}.");

    public static final Property<String> crate_requirements_need_more_room = PropertyInitializer.newProperty("crates.need-more-room", "<red>There is not enough space to open that here.");

    public static final Property<String> crate_requirements_world_disabled = PropertyInitializer.newProperty("crates.world-disabled", "<red>Crates are disabled in <yellow>{world}.");

    public static final Property<List<String>> crates_physical_crate_created = PropertyInitializer.newListProperty("crates.physical-crate.created",
            List.of(
                    "<gray>You have set that block to <yellow>{crate}.",
                    "<gray>To remove <yellow>{crate}, <gray>Shift Click Break in Creative to remove."
            ));

    public static final Property<String> crates_physical_crate_removed = PropertyInitializer.newProperty("crates.physical-crate.removed", "<gray>You have removed <yellow>{id}.");

    // Commands

    public static final Property<String> command_open_crate = PropertyInitializer.newProperty("command.open.opened-a-crate", "<gray>You have opened the <yellow>{crate} <gray>crate for <yellow>{player}.");

    public static final Property<String> command_give_player_keys = PropertyInitializer.newProperty("command.give.given-player-keys", "<gray>You have given <yellow>{player} {amount} Keys.");

    @Comment("This is only sent if they have a full inventory.")
    public static final Property<String> command_give_cannot_give_player_keys = PropertyInitializer.newProperty("command.give.cannot-give-player-keys", "<gray>You have been given <yellow>{amount} {key} <gray>virtual keys because your inventory is full.");

    public static final Property<String> command_give_everyone_keys = PropertyInitializer.newProperty("command.give.given-everyone-keys", "<gray>You have given everyone <yellow>{amount} Keys.");

    public static final Property<String> command_give_offline_player_keys = PropertyInitializer.newProperty("command.give.given-offline-player-keys", "<gray>You have given <yellow>{amount} key(s) <gray>to the offline player <yellow>{player}.");

    public static final Property<String> command_take_player_keys = PropertyInitializer.newProperty("command.take.take-player-keys", "<gray>You have taken <yellow>{amount} key(s) <gray>from <yellow>{player}.");

    public static final Property<String> command_take_offline_player_keys = PropertyInitializer.newProperty("command.take.take-offline-player-keys", "<gray>You have taken <yellow>{amount} key(s) <gray>from the offline player <yellow>{player}.");

    public static final Property<String> command_add_item_no_item_in_hand = PropertyInitializer.newProperty("command.additem.no-item-in-hand", "<red>You need to have an item in your hand to add it to <yellow>{crate}.");

    public static final Property<String> command_add_item_from_hand = PropertyInitializer.newProperty("command.additem.add-item-from-hand", "<gray>The item has been added to <yellow>{crate} <gray>as <yellow>Prize #{prize}.");

    public static final Property<String> command_convert_no_files_to_convert = PropertyInitializer.newProperty("command.convert.no-files-to-convert", "<red>No available plugins to convert files.");

    public static final Property<String> command_convert_error_converting_files = PropertyInitializer.newProperty("command.convert.error-converting-files", "<red>An error has occurred while trying to convert files. We could not convert <yellow>{file} <red>so please check the console.");

    public static final Property<String> command_convert_successfully_converted_files = PropertyInitializer.newProperty("command.convert.successfully-converted-files", "<green>Plugin Conversion has succeeded!");

    public static final Property<String> command_reload_completed = PropertyInitializer.newProperty("command.reload", "<green>You have reloaded the plugin.");

    public static final Property<String> command_transfer_not_enough_keys = PropertyInitializer.newProperty("command.transfer.not-enough-keys", "<red>You do not have enough keys to transfer.");

    public static final Property<String> command_transfer_keys = PropertyInitializer.newProperty("command.transfer.transferred-keys", "<gray>You have transferred <red>{amount} {crate} <gray>keys to <red>{player}.");

    public static final Property<String> command_transfer_keys_received = PropertyInitializer.newProperty("command.transfer.transferred-keys-received", "<gray>You have received <red>{amount} {crate} <gray>keys from <red>{player}.");

    public static final Property<String> command_keys_personal_no_virtual_keys = PropertyInitializer.newProperty("command.keys.personal.no-virtual-keys", "<dark_gray>(<yellow>!<dark_gray>) <gray>You currently do not have any virtual keys.");

    public static final Property<List<String>> command_keys_personal_virtual_keys_header = PropertyInitializer.newListProperty("command.keys.personal.virtual-keys-header", List.of(
            "<dark_gray>(<yellow>!<dark_gray>) <gray>A list of your current amount of keys."
    ));

    public static final Property<String> command_keys_other_player_no_virtual_keys = PropertyInitializer.newProperty("command.keys.other-player.no-virtual-keys", "<dark_gray>(<yellow>!<dark_gray>) <gray>The player <red>{player} <gray>does not have any keys.");

    public static final Property<List<String>> command_keys_other_player_virtual_keys_header = PropertyInitializer.newListProperty("command.keys.other-player.virtual-keys-header", List.of(
            "<dark_gray>(<yellow>!<dark_gray>) <gray>A list of <red>{player}''s <gray>current amount of keys."
    ));

    @Comment("This is related to what will show in the output above.")
    public static final Property<String> command_keys_crate_format = PropertyInitializer.newProperty("command.keys.crate-format", "{crate} <dark_gray>» <yellow>{keys} keys.");

    public static final Property<List<String>> player_help = PropertyInitializer.newListProperty("command.help.player-help", List.of(
            " <green>Crazy Crates Player Help!",
            " ",
            " <dark_gray>» <yellow>/key [player] <gray>» <gold>Check how many keys a player has.",
            " <dark_gray>» <yellow>/cc <gray>» <gold>Opens the crate menu."
    ));

    public static final Property<List<String>> admin_help = PropertyInitializer.newListProperty("command.help.admin-help", List.of(
            " <red>Crazy Crates Admin Help",
            " ",
            " <dark_gray>» <yellow>/cc additem [crate] [prize] <gray>- <gold>Add items in-game to a prize in a crate.",
            " <dark_gray>» <yellow>/cc preview [crate] [player] <gray>- <gold>Opens the preview of a crate for a player.",
            " <dark_gray>» <yellow>/cc list <gray>- <gold>Lists all crates.",
            " <dark_gray>» <yellow>/cc open [crate] [player] <gray>- <gold>Tries to open a crate for a player if they have a key.",
            " <dark_gray>» <yellow>/cc forceopen [crate] [player] <gray>- <gold>Opens a crate for a player for free.",
            " <dark_gray>» <yellow>/cc tp [location] <gray>- <gold>Teleport to a Crate.",
            " <dark_gray>» <yellow>/cc give [physical/virtual] [crate] [amount] [player] <gray>- <gold>Allows you to take keys from a player.",
            " <dark_gray>» <yellow>/cc set [crate] <gray>- <gold>Set the block you are looking at as a crate.",
            " <dark_gray>» <yellow>/cc set Menu <gray>- <gold>Set the block you are looking at to open the <red>/cc menu.",
            " <dark_gray>» <yellow>/cc reload <gray>- <gold>Reloads the config/data files.",
            " <dark_gray>» <yellow>/cc set1/set2 <gray>- <gold>Sets position <red>#1 or #2 for when making a new schematic for QuadCrates.",
            " <dark_gray>» <yellow>/cc save [file name] <gray>- <gold>Create a new nbt file in the schematics folder.",
            " <dark_gray>» <yellow>/cc mass-open [amount] <gray>- <gold>Mass opens crates. Defaults to 10 but can be changed in the crate config files.",
            " ",
            " <dark_gray>» <yellow>/key [player] <gray>- <gold>Check how many keys a player has.",
            " <dark_gray>» <yellow>/cc <gray>- <gold>Opens the crate menu.",
            " ",
            "<gray>You can find a list of permissions @ <gold>https://docs.crazycrew.us/crazycrates/info/commands/permissions"
    ));
}