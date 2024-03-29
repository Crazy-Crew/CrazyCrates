package us.crazycrew.crazycrates.platform.config.impl.messages;

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
                ""
        };

        conf.setComment("misc", header);
    }

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> unknown_command = newProperty("misc.unknown-command", "{prefix}&cThis command is not known.");

    @Comment("Only activates when you try to use an ender pearl while opening a crate.")
    public static final Property<String> no_teleporting = newProperty("misc.no-teleporting", "{prefix}&cYou may not teleport away while opening a crate.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> no_commands_while_using_crate = newProperty("misc.no-commands", "{prefix}&cYou are not allowed to use commands while opening crates.");

    @Comment("A list of available placeholders: {prefix}, {key}, {crate}")
    public static final Property<String> no_keys = newProperty("misc.no-keys", "{prefix}&cYou must have a {key} &cto use &6{crate}.");

    @Comment("A list of available placeholders: {prefix}, {key}, {crate}")
    public static final Property<String> no_virtual_key = newProperty("misc.no-virtual-keys", "{prefix}&cYou need a virtual key to open &6{crate}.");

    @Comment("A list of available placeholders: {prefix}, {usage}")
    public static final Property<String> correct_usage = newProperty("misc.correct-usage", "{prefix}&cThe correct usage for this command is &e{usage}");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> feature_disabled = newProperty("misc.feature-disabled", "{prefix}&cThis feature is disabled.");
}