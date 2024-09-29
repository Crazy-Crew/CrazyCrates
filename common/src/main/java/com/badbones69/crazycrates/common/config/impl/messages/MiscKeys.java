package com.badbones69.crazycrates.common.config.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class MiscKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "",
                "All messages allow the use of {prefix} unless stated otherwise.",
                ""
        };

        conf.setComment("misc", header);
    }

    @Comment("A list of available placeholders: {command}")
    public static final Property<String> unknown_command = newProperty("misc.unknown-command", "{prefix}<red>{command} is not a known command.");

    @Comment("Only activates when you try to use an ender pearl while opening a crate.")
    public static final Property<String> no_teleporting = newProperty("misc.no-teleporting", "{prefix}<red>You may not teleport away while opening a crate.");

    public static final Property<String> no_commands_while_using_crate = newProperty("misc.no-commands", "{prefix}<red>You are not allowed to use commands while opening crates.");

    @Comment("A list of available placeholders: {key}, {crate}")
    public static final Property<String> no_keys = newProperty("misc.no-keys", "{prefix}<red>You must have a {key} <red>in your hand to use <gold>{crate}.");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> no_virtual_key = newProperty("misc.no-virtual-keys", "{prefix}<red>You need a virtual key to open <gold>{crate}.");

    @Comment("A list of available placeholders: {usage}")
    public static final Property<String> correct_usage = newProperty("misc.correct-usage", "{prefix}<red>The correct usage for this command is <yellow>{usage}");

    public static final Property<String> feature_disabled = newProperty("misc.feature-disabled", "{prefix}<red>This feature is disabled.");
}