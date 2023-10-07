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

    public static final Property<String> unknown_command = PropertyInitializer.newProperty("misc.unknown-command", "&cThis command is not known.");

    public static final Property<String> no_teleporting = PropertyInitializer.newProperty("misc.no-teleporting", "&cYou may not teleport away while opening &e{crate}.");

    public static final Property<String> no_commands = PropertyInitializer.newProperty("misc.no-commands", "&cYou are not allowed to use commands while opening &e{crate}.");

    public static final Property<String> no_keys = PropertyInitializer.newProperty("misc.no-keys", "&cYou need a {key} &cin your hand to use &e{crate}.");

    public static final Property<String> no_virtual_keys = PropertyInitializer.newProperty("misc.no-virtual-keys", "&cYou need {key} &cto open &e{crate}.");

    public static final Property<String> feature_disabled = PropertyInitializer.newProperty("misc.feature-disabled", "&cThis feature is disabled. We have no ETA on when this will function.");

    public static final Property<String> correct_usage = PropertyInitializer.newProperty("misc.correct-usage", "&cThe correct usage for this command is &6{usage}");

    // Errors
    public static final Property<String> no_prizes_found = PropertyInitializer.newProperty("errors.no-prizes-found", "&cThis crate contains no prizes that you can win.");

    public static final Property<String> key_refund = PropertyInitializer.newProperty("errors.key-refund", "&cThere was an error with prize &e{crate} &cso you've been refunded your key");

    public static final Property<String> no_schematics_found = PropertyInitializer.newProperty("errors.no-schematics-found", "&cNo schematic were found, Please re-generate them by deleting the folder or checking for errors!");

    public static final Property<List<String>> prize_error = PropertyInitializer.newListProperty("errors.prize-error", List.of(
            "&cAn error has occurred while trying to give you the prize &e{prize}.",
            "&6This has occurred in &e{crate}. &6Please notify your owner."
    ));

    public static final Property<String> internal_error = PropertyInitializer.newProperty("errors.internal-error", "&cAn internal error has occurred. Please check the console for the full error.");

    // Players
    public static final Property<String> player_requirements_must_be_player = PropertyInitializer.newProperty("player.requirements.must-be-player", "&cYou must be a player to use this command.");

    public static final Property<String> player_requirements_must_be_console_sender = PropertyInitializer.newProperty("player.requirements.must-be-console-sender", "&cYou must be using console to use this command.");

    public static final Property<String> player_requirements_must_be_looking_at_block = PropertyInitializer.newProperty("player.requirements.must-be-looking-at-block", "&cYou must be looking at a block.");

    public static final Property<String> player_requirements_target_not_online = PropertyInitializer.newProperty("player.target-not-online", "&cThe player &e{player} &cis not online.");

    public static final Property<String> player_requirements_same_player = PropertyInitializer.newProperty("player.target-same-player", "&cYou cannot use this command on yourself.");

    public static final Property<String> player_requirements_no_permission = PropertyInitializer.newProperty("player.no-permission", "&cYou do not have permission to use that command!");

    public static final Property<String> player_requirements_inventory_not_empty = PropertyInitializer.newProperty("player.inventory-not-empty", "&cInventory is not empty, Please make room before opening &e{crate}.");

    public static final Property<String> player_requirements_obtaining_keys = PropertyInitializer.newProperty("player.obtaining-keys", "&7You have been given &e{amount} {key} &7Keys.");

    public static final Property<String> player_requirements_too_close_to_another_player = PropertyInitializer.newProperty("player.too-close-to-another-player", "&cYou are too close to a player that is opening their Crate.");

    public static final Property<String> player_requirements_required_keys = PropertyInitializer.newProperty("player.required-keys", "&7You need &c{key-amount} &7keys to open &c{crate}. &7You have &c{amount}.");

    // Crates
    public static final Property<String> crate_requirements_not_a_crate = PropertyInitializer.newProperty("crates.requirements.not-a-crate", "&cThere is no crate called &e{crate}.");

    public static final Property<String> crate_requirements_no_permission = PropertyInitializer.newProperty("crates.requirements.no-permission", "&cYou do not have permission to use that crate");

    public static final Property<String> crate_requirements_not_a_number = PropertyInitializer.newProperty("crates.requirements.not-a-number", "&e{number} &cis not a number.");
    public static final Property<String> crate_requirements_not_on_block = PropertyInitializer.newProperty("crates.requirements.not-on-block", "&cYou must be standing on a block to use &e{crate}.");

    public static final Property<String> crate_requirements_out_of_time = PropertyInitializer.newProperty("crates.out-of-time", "&cYou took &a5 Minutes &cto open the &e{crate} &cso it closed.");

    public static final Property<String> crate_requirements_preview_disabled = PropertyInitializer.newProperty("crates.crate-preview-disabled", "&cThe preview for &e{crate} &7is currently disabled.");

    public static final Property<String> crate_requirements_already_open = PropertyInitializer.newProperty("crates.crate-already-open", "&cYou are already opening &e{crate}.");

    public static final Property<String> crate_requirements_in_use = PropertyInitializer.newProperty("crates.crate-in-use", "&e{crate} &cis already in use. Please wait until it finishes!");

    public static final Property<String> crate_requirements_cannot_be_a_virtual_crate = PropertyInitializer.newProperty("crates.cannot-be-a-virtual-crate", "&e{crate} &ccannot be used as a Virtual Crate. You have it set to &e{cratetype}.");

    public static final Property<String> crate_requirements_need_more_room = PropertyInitializer.newProperty("crates.need-more-room", "&cThere is not enough space to open that here.");

    public static final Property<String> crate_requirements_world_disabled = PropertyInitializer.newProperty("crates.world-disabled", "&cCrates are disabled in &e{world}.");

    public static final Property<List<String>> crates_physical_crate_created = PropertyInitializer.newListProperty("crates.physical-crate.created",
            List.of(
                    "&7You have set that block to &e{crate}.",
                    "&7To remove &e{crate}, &7Shift Click Break in Creative to remove."
            ));

    public static final Property<String> crates_physical_crate_removed = PropertyInitializer.newProperty("crates.physical-crate.removed", "&7You have removed &e{id}.");

    // Commands

    public static final Property<String> command_open_crate = PropertyInitializer.newProperty("command.open.opened-a-crate", "&7You have opened the &e{crate} &7crate for &e{player}.");

    public static final Property<String> command_give_player_keys = PropertyInitializer.newProperty("command.give.given-player-keys", "&7You have given &e{player} {amount} Keys.");

    @Comment("This is only sent if they have a full inventory.")
    public static final Property<String> command_give_cannot_give_player_keys = PropertyInitializer.newProperty("command.give.cannot-give-player-keys", "&7You have been given &e{amount} {key} &7virtual keys because your inventory is full.");

    public static final Property<String> command_give_everyone_keys = PropertyInitializer.newProperty("command.give.given-everyone-keys", "&7You have given everyone &e{amount} Keys.");

    public static final Property<String> command_give_offline_player_keys = PropertyInitializer.newProperty("command.give.given-offline-player-keys", "&7You have given &e{amount} key(s) &7to the offline player &e{player}.");

    public static final Property<String> command_take_player_keys = PropertyInitializer.newProperty("command.take.take-player-keys", "&7You have taken &e{amount} key(s) &7from &e{player}.");

    public static final Property<String> command_take_offline_player_keys = PropertyInitializer.newProperty("command.take.take-offline-player-keys", "&7You have taken &e{amount} key(s) &7from the offline player &e{player}.");

    public static final Property<String> command_add_item_no_item_in_hand = PropertyInitializer.newProperty("command.additem.no-item-in-hand", "&cYou need to have an item in your hand to add it to &e{crate}.");

    public static final Property<String> command_add_item_from_hand = PropertyInitializer.newProperty("command.additem.add-item-from-hand", "&7The item has been added to &e{crate} &7as &ePrize #{prize}.");

    public static final Property<String> command_convert_no_files_to_convert = PropertyInitializer.newProperty("command.convert.no-files-to-convert", "&cNo available plugins to convert files.");

    public static final Property<String> command_convert_error_converting_files = PropertyInitializer.newProperty("command.convert.error-converting-files", "&cAn error has occurred while trying to convert files. We could not convert &e{file} &cso please check the console.");

    public static final Property<String> command_convert_successfully_converted_files = PropertyInitializer.newProperty("command.convert.successfully-converted-files", "&aPlugin Conversion has succeeded!");

    public static final Property<String> command_reload_completed = PropertyInitializer.newProperty("command.reload.reload-completed", "&aPlugin reload has been completed.");

    public static final Property<String> command_transfer_not_enough_keys = PropertyInitializer.newProperty("command.transfer.not-enough-keys", "&cYou do not have enough keys to transfer.");

    public static final Property<String> command_transfer_keys = PropertyInitializer.newProperty("command.transfer.transferred-keys", "&7You have transferred &c{amount} {crate} &7keys to &c{player}.");

    public static final Property<String> command_transfer_keys_received = PropertyInitializer.newProperty("command.transfer.transferred-keys-received", "&7You have received &c{amount} {crate} &7keys from &c{player}.");

    public static final Property<String> command_keys_personal_no_virtual_keys = PropertyInitializer.newProperty("command.keys.personal.no-virtual-keys", "&8(&e!&8) &7You currently do not have any virtual keys.");

    public static final Property<List<String>> command_keys_personal_virtual_keys_header = PropertyInitializer.newListProperty("command.keys.personal.virtual-keys-header", List.of(
            "&8(&e!&8) &7A list of your current amount of keys."
    ));

    public static final Property<String> command_keys_other_player_no_virtual_keys = PropertyInitializer.newProperty("command.keys.other-player.no-virtual-keys", "&8(&e!&8) &7The player &c{player} &7does not have any keys.");

    public static final Property<List<String>> command_keys_other_player_virtual_keys_header = PropertyInitializer.newListProperty("command.keys.other-player.virtual-keys-header", List.of(
            "&8(&e!&8) &7A list of &c{player}''s &7current amount of keys."
    ));

    @Comment("This is related to what will show in the output above.")
    public static final Property<String> command_keys_crate_format = PropertyInitializer.newProperty("command.keys.crate-format", "{crate} &8» &e{keys} keys.");

    public static final Property<List<String>> player_help = PropertyInitializer.newListProperty("command.help.player-help", List.of(
            " &2Crazy Crates Player Help!",
            " ",
            " &8» &e/key [player] &7» &6Check how many keys a player has.",
            " &8» &e/cc &7» &6Opens the crate menu."
    ));

    public static final Property<List<String>> admin_help = PropertyInitializer.newListProperty("command.help.admin-help", List.of(
            " &cCrazy Crates Admin Help",
            " ",
            " &8» &e/cc additem [crate] [prize] &7- &6Add items in-game to a prize in a crate.",
            " &8» &e/cc preview [crate] [player] &7- &6Opens the preview of a crate for a player.",
            " &8» &e/cc list &7- &6Lists all crates.",
            " &8» &e/cc open [crate] [player] &7- &6Tries to open a crate for a player if they have a key.",
            " &8» &e/cc forceopen [crate] [player] &7- &6Opens a crate for a player for free.",
            " &8» &e/cc tp [location] &7- &6Teleport to a Crate.",
            " &8» &e/cc give [physical/virtual] [crate] [amount] [player] &7- &6Allows you to take keys from a player.",
            " &8» &e/cc set [crate] &7- &6Set the block you are looking at as a crate.",
            " &8» &e/cc set Menu &7- &6Set the block you are looking at to open the &c/cc menu.",
            " &8» &e/cc reload &7- &6Reloads the config/data files.",
            " &8» &e/cc set1/set2 &7- &6Sets position &c#1 or #2 for when making a new schematic for QuadCrates.",
            " &8» &e/cc save [file name] &7- &6Create a new nbt file in the schematics folder.",
            " &8» &e/cc mass-open [amount] &7- &6Mass opens crates. Defaults to 10 but can be changed in the crate config files.",
            " ",
            " &8» &e/key [player] &7- &6Check how many keys a player has.",
            " &8» &e/cc &7- &6Opens the crate menu.",
            " ",
            "&7You can find a list of permissions @ &6https://docs.crazycrew.us/crazycrates/info/commands/v2/permissions"
    ));
}