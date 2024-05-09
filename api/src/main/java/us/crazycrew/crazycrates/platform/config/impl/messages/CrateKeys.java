package us.crazycrew.crazycrates.platform.config.impl.messages;

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
    public static final Property<String> not_a_crate = newProperty("crates.requirements.not-a-crate", "{prefix}<red>There is no crate called <gold>{crate}.");

    @Comment("A list of available placeholders: {prefix}, {key}")
    public static final Property<String> not_a_key = newProperty("crates.requirements.not-a-key", "{prefix}<red>There is no key called <gold>{key}.");

    @Comment("A list of available placeholders: {prefix}, {number}")
    public static final Property<String> not_a_number = newProperty("crates.requirements.not-a-number", "{prefix}<gold>{number} <red>is not a number.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {key_amount}, {crate}")
    public static final Property<String> required_keys = newProperty("crates.requirements.not-enough-keys", "{prefix}<gray>You need <red>{key_amount} <gray>keys to open <red>{crate}. <gray>You have <red>{amount}.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> not_on_block = newProperty("crates.not-a-block", "{prefix}<red>You must be standing on a block to use <gold>{crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> out_of_time = newProperty("crates.out-of-time", "{prefix}<red>You took <gold>5 minutes <red>to open <gold>{crate} <red>so it closed.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> reloaded_forced_out_of_preview = newProperty("crates.forced-out-of-preview", "{prefix}<red>A reload has forced you out of the preview.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> cannot_set_type = newProperty("crates.cannot-set-menu-type", "{prefix}<red>You cannot set the Menu to a block because the crate menu is disabled");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> no_crate_permission = newProperty("crates.crate-no-permission", "{prefix}<red>You do not have permission to use that {crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> preview_disabled = newProperty("crates.crate-preview-disabled", "{prefix}<red>The preview for <gold>{crate} <red>is currently disabled.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> already_opening_crate = newProperty("crates.crate-already-open", "{prefix}<red>You are already opening <gold>{crate}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> crate_in_use = newProperty("crates.crate-in-use", "{prefix}<red>{crate} is already in use. Please wait until it finishes.");

    @Comment("A list of available placeholders: {prefix}, {crate}, {cratetype}")
    public static final Property<String> cant_be_a_virtual_crate = newProperty("crates.cannot-be-a-virtual-crate", "{prefix}<gold>{crate} <red>cannot be used as a Virtual Crate.");

    @Comment("QuadCrate schematics need a fair bit of room so make sure you check the surroundings.")
    public static final Property<String> needs_more_room = newProperty("crates.need-more-room", "{prefix}<red>There is not enough space to open that here");

    @Comment("A list of available placeholders: {prefix}, {world}")
    public static final Property<String> world_disabled = newProperty("crates.world-disabled", "{prefix}<red>I am sorry but Crates are disabled in {world}.");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<List<String>> created_physical_crate = newListProperty("crates.physical-crate.created", List.of(
            "{prefix}<gray>You have set that block to {crate}.",
            "<gray>To remove the crate shift break in creative to remove."
    ));

    @Comment("A list of available placeholders: {prefix}, {id}")
    public static final Property<String> removed_physical_crate = newProperty("crates.physical-crate.removed", "{prefix}<gray>You have removed <gold>{id}.");
}