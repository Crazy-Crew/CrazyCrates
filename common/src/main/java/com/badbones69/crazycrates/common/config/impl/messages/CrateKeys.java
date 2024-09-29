package com.badbones69.crazycrates.common.config.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class CrateKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to crates."
        };

        conf.setComment("crates", header);
    }

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> not_a_crate = newProperty("crates.requirements.not-a-crate", "{prefix}<red>There is no crate called <gold>{crate}.");

    @Comment("A list of available placeholders: {key}")
    public static final Property<String> not_a_key = newProperty("crates.requirements.not-a-key", "{prefix}<red>There is no key called <gold>{key}.");

    @Comment("A list of available placeholders: {number}")
    public static final Property<String> not_a_number = newProperty("crates.requirements.not-a-number", "{prefix}<gold>{number} <red>is not a number.");

    @Comment("A list of available placeholders: {amount}, {required_amount}, {crate}")
    public static final Property<String> not_enough_keys = newProperty("crates.requirements.not-enough-keys", "{prefix}<gray>You need <red>{key_amount} <gray>keys to open <red>{crate}. <gray>You have <red>{amount}.");

    public static final Property<String> not_on_block = newProperty("crates.not-a-block", "{prefix}<red>You must be standing on a block to use <gold>{crate}.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> out_of_time = newProperty("crates.out-of-time", "{prefix}<red>You took <gold>5 minutes <red>to open <gold>{crate} <red>so it closed.");

    public static final Property<String> reloaded_forced_out_of_preview = newProperty("crates.forced-out-of-preview", "{prefix}<red>A reload has forced you out of the preview.");

    public static final Property<String> cannot_set_type = newProperty("crates.cannot-set-menu-type", "{prefix}<red>You cannot set the Menu to a block because the crate menu is disabled");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> no_crate_permission = newProperty("crates.crate-no-permission", "{prefix}<red>You do not have permission to use {crate}.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> preview_disabled = newProperty("crates.crate-preview-disabled", "{prefix}<red>The preview for <gold>{crate} <red>is currently disabled.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> already_opening_crate = newProperty("crates.crate-already-open", "{prefix}<red>You are already opening <gold>{crate}.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> crate_in_use = newProperty("crates.crate-in-use", "{prefix}<red>{crate} is already in use. Please wait until it finishes.");

    public static final Property<String> already_redeemed_prize = newProperty("crates.already-redeemed-prize", "{prefix}<red>You have already redeemed this prize!");

    @Comment("A list of available placeholders: {crate}, {cratetype}")
    public static final Property<String> cant_be_a_virtual_crate = newProperty("crates.cannot-be-a-virtual-crate", "{prefix}<gold>{crate} <red>cannot be used as a Virtual Crate.");

    @Comment("QuadCrate schematics need a fair bit of room so make sure you check the surroundings.")
    public static final Property<String> needs_more_room = newProperty("crates.need-more-room", "{prefix}<red>There is not enough space to open that here");

    @Comment("A list of available placeholders: {world}")
    public static final Property<String> world_disabled = newProperty("crates.world-disabled", "{prefix}<red>I am sorry but Crates are disabled in {world}.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<List<String>> created_physical_crate = newListProperty("crates.physical-crate.created", List.of(
            "{prefix}<gray>You have set that block to {crate}.",
            "<gray>To remove the crate shift break in creative to remove."
    ));

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> physical_crate_already_exists = newProperty("crates.physical-crate.exists", "{prefix}<gray>This location already has a crate named <gold>{crate} <gray>with id: <gold>{id}.");

    @Comment("A list of available placeholders: {id}")
    public static final Property<String> removed_physical_crate = newProperty("crates.physical-crate.removed", "{prefix}<gray>You have removed <gold>{id}.");

    @Comment("The format for the /crazycrates list command")
    public static final Property<List<String>> crate_locations = newListProperty("crates.list.format", List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Crate Statistics ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Active Crates: ",
            " ⤷ {active_crates}</green>",
            "<dark_gray>»</dark_gray> <red>Broken Crates: ",
            " ⤷ {broken_crates}</red>",
            "<dark_gray>»</dark_gray> <yellow>Crate Locations: ",
            " ⤷ {active_locations}</yellow>",
            "",
            "{locations}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    ));

    @Comment("A list of available placeholders: {id}, {crate_name}, {world}, {x}, {y}, {z}")
    public static final Property<String> crate_location_format = newProperty("crates.list.per-crate", "<dark_gray>[<blue>{id}<dark_gray>]: <red>{crate_name}<dark_gray>, <red>{world}<dark_gray>, <red>{x}<dark_gray>, <red>{y}<dark_gray>, <red>{z}");

    @Comment("A list of available placeholders: {name}")
    public static final Property<String> crate_teleported = newProperty("crates.teleport.success", "<red>You have been teleported to the location with the name: <gold>{name}.");

    @Comment("A list of available placeholders: {id}")
    public static final Property<String> crate_cannot_teleport = newProperty("crates.teleport.failed", "{prefix}<red>There is no location with the name: <gold>{id}.");

    @Comment({
            "This will add a notice to a prize, if the prize has reached max pulls.",
            "If this message is empty, it will not add the lore to prizes.",
            "",
            "A list of available placeholders: {maxpulls}, {pulls}"
    })
    public static final Property<String> crate_prize_max_pulls = newProperty("crates.pulls.max", "{prefix}<red>This prize can no longer be obtained, {pulls}/{maxpulls}");

    @Comment("A list of available placeholders: {status}")
    public static final Property<String> crate_prize_max_respins = newProperty("crates.respins.max", "{prefix}<red>You can no longer respin, Status: {status}");

    @Comment({
            "If the player has any permission for respins, this format will show above in place of {status}",
            "",
            "A list of available placeholders: {respins_left}/{respins_total}"
    })
    public static final Property<String> crate_prize_max_respins_left = newProperty("crates.respins.format", "{respins_left}/{respins_total}");

    @Comment("A blank string to configure for {status}")
    public static final Property<String> crate_prize_max_respins_none = newProperty("crates.respins.none", "N/A");

}