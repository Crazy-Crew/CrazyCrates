package com.badbones69.crazycrates.common.config.types.messages;

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

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> not_a_crate = newProperty("crates.requirements.not-a-crate", "{prefix}&cThere is no crate called &6{crate}.");

    @Comment("A list of available placeholders: {prefix}, {number}")
    public static final Property<String> not_a_number = newProperty("crates.requirements.not-a-number", "{prefix}&6{number} &cis not a number.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {key_amount}, {crate}")
    public static final Property<String> required_keys = newProperty("crates.requirements.not-enough-keys", "{prefix}&7You need &c{key_amount} &7keys to open &c{crate}. &7You have &c{amount}.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> not_on_block = newProperty("crates.not-a-block", "{prefix}&cYou must be standing on a block to use &6{crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> out_of_time = newProperty("crates.out-of-time", "{prefix}&cYou took &65 minutes &cto open &6{crate} &cso it closed.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> reloaded_forced_out_of_preview = newProperty("crates.forced-out-of-preview", "{prefix}&cA reload has forced you out of the preview.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> cannot_set_type = newProperty("crates.cannot-set-menu-type", "{prefix}&cYou cannot set the Menu to a block because the crate menu is disabled");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> no_crate_permission = newProperty("crates.crate-no-permission", "{prefix}&cYou do not have permission to use that {crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> preview_disabled = newProperty("crates.crate-preview-disabled", "{prefix}&cThe preview for &6{crate} &cis currently disabled.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> already_opening_crate = newProperty("crates.crate-already-open", "{prefix}&cYou are already opening &6{crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> quick_crate_in_use = newProperty("crates.crate-in-use", "{prefix}&c{crate} is already in use. Please wait until it finishes.");

    @Comment("A list of available placeholders: {prefix}, {crate}, {cratetype}")
    public static final Property<String> cant_be_a_virtual_crate = newProperty("crates.cannot-be-a-virtual-crate", "{prefix}&6{crate} &ccannot be used as a Virtual Crate.");

    @Comment("QuadCrate schematics need a fair bit of room so make sure you check the surroundings.")
    public static final Property<String> needs_more_room = newProperty("crates.need-more-room", "{prefix}&cThere is not enough space to open that here");

    @Comment("A list of available placeholders: {prefix}, {world}")
    public static final Property<String> world_disabled = newProperty("crates.world-disabled", "{prefix}&cI am sorry but Crates are disabled in {world}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<List<String>> created_physical_crate = newListProperty("crates.physical-crate.created", List.of(
            "{prefix}&7You have set that block to {crate}.",
            "&7To remove the crate shift break in creative to remove."
    ));

    @Comment("A list of available placeholders: {prefix}, {id}")
    public static final Property<String> removed_physical_crate = newProperty("crates.physical-crate.removed", "{prefix}&7You have removed &6{id}.");
}