package com.badbones69.crazycrates.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MessageKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                ""
        };

        conf.setComment("Messages", header);
    }

    @Comment("Only activates when you try to use an ender pearl while opening a crate.")
    public static final Property<String> no_teleporting = newProperty("Messages.No-Teleporting", "%prefix%&cYou may not teleport away while opening a Crate.");

    public static final Property<String> no_commands_while_using_crate = newProperty("Messages.No-Commands-While-In-Crate", "%prefix%&cYou are not allowed to use commands while opening Crates.");

    public static final Property<String> feature_disabled = newProperty("Messages.Feature-Disabled", "%prefix%&cThis feature is disabled.");

    public static final Property<String> no_keys = newProperty("Messages.No-Key", "%prefix%&cYou must have a %key% &cin your hand to use that crate.");

    public static final Property<String> no_virtual_key = newProperty("Messages.No-Virtual-Key", "%prefix%&cYou need a key to open that Crate.");

    public static final Property<String> not_on_block = newProperty("Messages.Not-On-Block", "%prefix%&cYou must be standing on a block to use this Crate.");

    public static final Property<String> already_opening_crate = newProperty("Messages.Already-Opening-Crate", "%prefix%&cYou are already opening a Crate.");

    public static final Property<String> quick_crate_in_use = newProperty("Messages.Quick-Crate-In-Use", "%prefix%&cThat Crate is already in use. Please wait for the Crate to open up.");

    public static final Property<String> world_disabled = newProperty("Messages.World-Disabled", "%prefix%&cI am sorry but Crates are disabled in %world%.");

    public static final Property<String> reloaded_plugin = newProperty("Messages.Reload", "%prefix%&3You have reloaded the Config and Data Files.");

    public static final Property<String> reloaded_forced_out_of_preview = newProperty("Messages.Forced-Out-Of-Preview", "%prefix%&cA reload has forced you out of the preview.");

    public static final Property<String> not_online = newProperty("Messages.Not-Online", "%prefix%&c%player% &7is not online.");

    public static final Property<String> no_permission = newProperty("Messages.No-Permission", "%prefix%&cYou do not have permission to use that command/menu!");

    public static final Property<String> no_crate_permission = newProperty("Messages.No-Crate-Permission", "%prefix%&cYou do not have permission to use that crate.");

    public static final Property<String> cant_be_a_virtual_crate = newProperty("Messages.Cant-Be-A-Virtual-Crate", "%prefix%&cThat Crate type cannot be used as a Virtual Crate.");

    public static final Property<String> inventory_not_empty = newProperty("Messages.Inventory-Full", "%prefix%&cYour inventory is full, Please make room before opening a Crate.");

    public static final Property<String> too_close_to_another_player = newProperty("Messages.To-Close-To-Another-Player", "%prefix%&cYou are too close to a player that is opening their Crate.");

    @Comment("QuadCrate schematics need a fair bit of room so make sure you check the surroundings.")
    public static final Property<String> needs_more_room = newProperty("Messages.Needs-More-Room", "%prefix%&cThere is not enough space to open that here");

    public static final Property<String> out_of_time = newProperty("Messages.Out-Of-Time", "%prefix%&cYou took 5 Minutes to open the Crate so it closed.");

    public static final Property<String> must_be_a_player = newProperty("Messages.Must-Be-A-Player", "%prefix%&cYou must be a player to use this command.");

    public static final Property<String> must_be_console_sender = newProperty("Messages.Must-Be-A-Console-Sender", "%prefix%&cYou must be using console to use this command.");

    public static final Property<String> must_be_looking_at_block = newProperty("Messages.Must-Be-Looking-At-A-Block", "%prefix%&cYou must be looking at a block.");

    public static final Property<String> not_a_crate = newProperty("Messages.Not-A-Crate", "%prefix%&cThere is no crate called &6%crate%.");

    public static final Property<String> not_a_number = newProperty("Messages.Not-A-Number", "%prefix%&6%number% &cis not a number.");

    public static final Property<String> gave_a_player_keys = newProperty("Messages.Given-A-Player-Keys", "%prefix%&7You have given &6%player% %amount% &7Keys.");

    public static final Property<String> cannot_give_player_keys = newProperty("Messages.Cannot-Give-Player-Keys", "%prefix%&7You have been given &6%amount% %key% &7virtual keys because your inventory was full.");

    public static final Property<String> obtaining_keys = newProperty("Messages.Obtaining-Keys", "%prefix%&7You have been given &6%amount% %key% &7Keys.");

    public static final Property<String> given_everyone_keys = newProperty("Messages.Given-Everyone-Keys", "%prefix%&7You have given everyone &6%amount% &7Keys.");

    public static final Property<String> given_offline_player_keys = newProperty("Messages.Given-Offline-Player-Keys", "%prefix%&7You have given &6%amount% &7key(s) to the offline player &6%player%.");

    public static final Property<String> take_players_keys = newProperty("Messages.Take-A-Player-Keys", "%prefix%&7You have taken &6%amount% &7key(s) from &6%player%.");

    public static final Property<String> cannot_take_keys = newProperty("Messages.Cannot-Take-Keys", "%prefix%&7You cannot take keys from &6%player% &7as they are poor.");

    public static final Property<String> take_offline_player_keys = newProperty("Messages.Take-Offline-Player-Keys", "%prefix%&7You have taken &6%amount% &7key(s) from the offline player &6%player%.");

    public static final Property<String> opened_a_crate = newProperty("Messages.Opened-A-Crate", "%prefix%&7You have just opened the &6%crate% &7crate for &6%player%.");

    public static final Property<String> internal_error = newProperty("Messages.Internal-Error", "%prefix%&cAn internal error has occurred. Please check the console for the full error.");

    public static final Property<String> unknown_command = newProperty("Messages.Unknown-Command", "%prefix%&cThis command is not known.");

    public static final Property<String> correct_usage = newProperty("Messages.Correct-Usage", "%prefix%&cThe correct usage for this command is &e%usage%");

    public static final Property<String> no_item_in_hand = newProperty("Messages.No-Item-In-Hand", "%prefix%&cYou need to have an item in your hand to add it to the Crate.");

    public static final Property<String> added_item_with_editor = newProperty("Messages.Added-Item-With-Editor", "%prefix%&7The item has been added to the %crate% Crate in prize #%prize%.");

    public static final Property<String> failed_to_add_item = newProperty("Messages.Failed-To-Add-Item", "%prefix%&cCannot add an item if the crate type is a CosmicCrate for the time being.");

    public static final Property<String> preview_disabled = newProperty("Messages.Preview-Disabled", "%prefix%&cThe preview for that crate is currently disabled.");

    public static final Property<String> no_schematics_found = newProperty("Messages.No-Schematics-Found", "%prefix%&cNo schematics were found. Please make sure files ending in .nbt exist in the schematics folder. If not delete the folder so they regenerate");

    public static final Property<String> no_prizes_found = newProperty("Messages.No-Prizes-Found", "%prefix%&cThis crate contains no prizes that you can win.");

    public static final Property<String> same_player = newProperty("Messages.Same-Player", "%prefix%&cYou can''t use this command on yourself.");

    public static final Property<String> prize_error = newProperty("Messages.Prize-Error", "%prefix%&cAn error has occurred while trying to give you the prize called &6%prize%&c in crate called &6%crate%&c. Please contact the server owner and show them this error.");

    public static final Property<String> required_keys = newProperty("Messages.Required-Keys", "%prefix%&7You need &c%key-amount% &7keys to open &c%crate%. &7You have &c%amount%.");

    public static final Property<String> transfer_not_enough_keys = newProperty("Messages.Transfer-Keys.Not-Enough-Keys", "%prefix%&cYou do not have enough keys to transfer.");

    public static final Property<String> transfer_sent_keys = newProperty("Messages.Transfer-Keys.Transferred-Keys", "%prefix%&7You have transferred %amount% %crate% keys to %player%.");

    public static final Property<String> transfer_received_keys = newProperty("Messages.Transfer-Keys.Received-Transferred-Keys", "%prefix%&7You have received %amount% %crate% keys from %player%.");

    public static final Property<List<String>> created_physical_crate = newListProperty("Messages.Created-Physical-Crate", List.of(
            "%prefix%&7You have set that block to %crate%.",
            "&7To remove the crate shift break in creative to remove."
    ));

    public static final Property<String> removed_physical_crate = newProperty("Messages.Removed-Physical-Crate", "%prefix%&7You have removed &6%id%.");

    public static final Property<String> no_virtual_keys = newProperty("Messages.Keys.Personal.No-Virtual-Keys", "%prefix%&8&l(&4&l!&8&l) &7You currently do not have any virtual keys.");

    public static final Property<List<String>> no_virtual_keys_header = newListProperty("Messages.Keys.Personal.Header", List.of(
            "&8&l(&6&l!&8&l) &7List of your current number of keys.",
            " &e -> Total Crates Opened: &c%crates_opened%"
    ));

    public static final Property<String> other_player_no_keys = newProperty("Messages.Keys.Other-Player.No-Virtual-Keys", "%prefix%&8&l(&4&l!&8&l) &7The player %player% does not have any keys.");

    public static final Property<List<String>> other_player_header = newListProperty("Messages.Keys.Other-Player.Header", List.of(
            "&8&l(&6&l!&8&l) &7List of %player%''s current number of keys.",
            " &e -> Total Crates Opened: &c%crates_opened%"
    ));

    public static final Property<String> per_crate = newProperty("Messages.Keys.Per-Crate", "%crate% &7&l>&8&l> &6%keys% keys &7: Opened &6%crate_opened% times");

    @Comment("The output of /crazycrates help | Requires crazycrates.command.help")
    public static final Property<List<String>> help = newListProperty("Messages.Help", List.of(
            "&e&lCrazy Crates Player Help",
            "&6/keys view [player] &7- &eCheck the number of keys a player has.",
            "&6/keys &7- &eShows how many keys you have.",
            "&6/cc &7- &eOpens the menu."
    ));

    @Comment("The output of /crazycrates admin-help | Requires crazycrates.command.admin.help")
    public static final Property<List<String>> admin_help = newListProperty("Messages.Admin-Help", List.of(
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