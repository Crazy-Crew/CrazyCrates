package com.badbones69.crazycrates.common.config.types.messages;

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

    @Comment("A list of available placeholders: {prefix}, {crate}, {player}")
    public static final Property<String> opened_a_crate = newProperty("command.open.opened-a-crate", "{prefix}&7You have opened the &6{crate} for &6{player}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> gave_a_player_keys = newProperty("command.give.given-player-keys", "{prefix}&7You have given &6{player} {amount} &7key(s).");

    @Comment("A list of available placeholders: {prefix}, {amount}, {amount}, {keytype}")
    public static final Property<String> cannot_give_player_keys = newProperty("command.give.full-inventory", "{prefix}&7You have been given &6{amount} {key} &7virtual key(s) because your inventory was full.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {keytype}")
    public static final Property<String> given_everyone_keys = newProperty("command.give.given-everyone-keys", "{prefix}&7You have given everyone &6{amount} &7key(s).");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> given_offline_player_keys = newProperty("command.give.given-offline-player-keys", "{prefix}&7You have given &6{amount} &7key(s) to the offline player &6{player}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> take_players_keys = newProperty("command.take.take-player-keys", "{prefix}&7You have taken &6{amount} &7key(s) from &6{player}.");

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> cannot_take_keys = newProperty("command.take.cannot-take-keys", "{prefix}&7You cannot take key(s) from &6{player} &7as they are poor.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}")
    public static final Property<String> take_offline_player_keys = newProperty("command.take.take-offline-player-keys", "{prefix}&7You have taken &6{amount} &7key(s) from the offline player &6{player}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> no_item_in_hand = newProperty("command.additem.no-item-in-hand", "{prefix}&cYou need to have an item in your hand to add it {crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}, {prize}")
    public static final Property<String> added_item_with_editor = newProperty("command.additem.add-item-from-hand", "{prefix}&7The item has been added to the {crate} in prize #{prize}.");

    public static final Property<String> no_files_to_convert = newProperty("command.convert.no-files-to-convert", "&cNo available plugins to convert files.");

    public static final Property<String> error_converting_files = newProperty("command.convert.error-converting-files", "&cAn error has occurred while trying to convert files. We could not convert &a{file} &cso please check the console.");

    public static final Property<String> successfully_converted_files = newProperty("command.successfully-converted-files", "&aPlugin Conversion has succeeded!");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> reloaded_plugin = newProperty("command.reload.completed", "{prefix}&3You have reloaded the Config and Data Files.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> transfer_not_enough_keys = newProperty("command.transfer.not-enough-keys", "{prefix}&cYou do not have enough keys to transfer.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}, {crate}")
    public static final Property<String> transfer_sent_keys = newProperty("command.transfer.transferred-keys", "{prefix}&7You have transferred {amount} {crate} keys to {player}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {player}, {keytype}, {crate}")
    public static final Property<String> transfer_received_keys = newProperty("command.transfer.transferred-keys-received", "{prefix}&7You have received {amount} {crate} keys from {player}.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> no_virtual_keys = newProperty("command.keys.personal.no-virtual-keys", "{prefix}&8&l(&4&l!&8&l) &7You currently do not have any virtual keys.");

    @Comment("A list of available placeholders: {crates_opened}")
    public static final Property<List<String>> virtual_keys_header = newListProperty("command.keys.personal.virtual-keys-header", List.of(
            "&8&l(&6&l!&8&l) &7List of your current number of keys.",
            " &e -> Total Crates Opened: &c{crates_opened}"
    ));

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> other_player_no_keys = newProperty("command.keys.other-player.no-virtual-keys", "{prefix}&8&l(&4&l!&8&l) &7The player {player} does not have any keys.");

    @Comment("A list of available placeholders: {player}, {crates_opened}")
    public static final Property<List<String>> other_player_header = newListProperty("command.keys.other-player.virtual-keys-header", List.of(
            "&8&l(&6&l!&8&l) &7List of {player}''s current number of keys.",
            " &e -> Total Crates Opened: &c{crates_opened}"
    ));

    @Comment("A list of available placeholders: {crate}, {keys}, {crate_opened}")
    public static final Property<String> per_crate = newProperty("command.keys.crate-format", "{crate} &7&l>&8&l> &6{keys} keys &7: Opened &6{crate_opened} times");

    @Comment("This requires crazycrates.command.help")
    public static final Property<List<String>> help = newListProperty("command.player-help", List.of(
            "&e&lCrazy Crates Player Help",
            "&6/keys view [player] &7- &eCheck the number of keys a player has.",
            "&6/keys &7- &eShows how many keys you have.",
            "&6/cc &7- &eOpens the menu."
    ));

    @Comment("This requires crazycrates.command.admin.help")
    public static final Property<List<String>> admin_help = newListProperty("command.admin-help", List.of(
            "&c&lCrazy Crates Admin Help",
            "",
            "&6/cc additem <crate_name> <prize_number> <chance> [tier] &7- &eAdd items in-game to a prize in a crate including Cosmic/Casino.",
            "&6/cc preview <crate_name> [player] &7- &eOpens the preview of a crate for a player.",
            "&6/cc list &7- &eLists all crates.",
            "&6/cc open <crate_name> &7- &eTries to open a crate for you if you have a key.",
            "&6/cc open-others <crate_name> [player] &7- &eTries to open a crate for a player if they have a key.",
            "&6/cc transfer <crate_name> [player] [amount &7- &eTransfers keys to players you chose.",
            "&6/cc debug &7- &eDebugs crates",
            "&6/cc admin &7- &eShows admin menu",
            "&6/cc forceopen <crate_name> [player] &7- &eOpens a crate for a player for free.",
            "&6/cc mass-open <crate_name> <physical/virtual> [amount] &7- &eMass opens a set amount of crates.",
            "&6/cc tp <location> &7- &eTeleport to a Crate.",
            "&6/cc give <physical/virtual> <crate_name> [amount] [player] &7- &eAllows you to take keys from a player.",
            "&6/cc set <crate_name> &7- &eSet the block you are looking at as a crate.",
            "&6/cc set Menu &7- &eSet the block you are looking at to open the /cc menu.",
            "&6/cc reload &7- &eReloads the config/data files.",
            "&6/cc set1/set2 &7- &eSets position &c#1 &eor &c#2 for when making a new schematic for QuadCrates.",
            "&6/cc save <file name> &7- &eCreate a new nbt file in the schematics folder.",
            "",
            "&6/keys view [player] &7- &eCheck the number of keys a player has.",
            "&6/keys &7- &eShows how many keys you have.",
            "&6/cc &7- &eOpens the menu.",
            "",
            "&7You can find a list of permissions @ &ehttps://docs.crazycrew.us/crazycrates/info/commands/permissions"
    ));
}