package com.badbones69.crazycrates.core.config.impl.messages;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class PlayerKeys implements SettingsHolder {

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "All messages related to players."
        };

        conf.setComment("player", header);
    }


    public static final Property<String> must_be_a_player = newProperty("player.requirements.must-be-player", "{prefix}<red>You must be a player to use this command.");

    public static final Property<String> must_be_console_sender = newProperty("player.requirements.must-be-console-sender", "{prefix}<red>You must be using console to use this command.");

    public static final Property<String> must_be_looking_at_block = newProperty("player.requirements.must-be-looking-at-block", "{prefix}<red>You must be looking at a block.");

    @Comment("A list of available placeholders: {player}")
    public static final Property<String> not_online = newProperty("player.target-not-online", "{prefix}<red>{player} <gray>is not online.");

    public static final Property<String> same_player = newProperty("player.target-same-player", "{prefix}<red>You can't use this command on yourself.");

    public static final Property<String> no_permission = newProperty("player.no-permission", "{prefix}<red>You do not have permission to use that command/menu!");

    @Comment("A list of available placeholders: {crate}")
    public static final Property<String> inventory_not_empty = newProperty("player.inventory-not-empty", "{prefix}<red>Inventory is not empty, Please make room before opening <gold>{crate}.");

    @Comment("A list of available placeholders: {amount}, {keytype}, {key}")
    public static final Property<String> obtaining_keys = newProperty("player.obtaining-keys", "{prefix}<gray>You have been given <gold>{amount} {key} <gray>key(s).");

    @Comment("A list of available placeholders: {player}")
    public static final Property<String> too_close_to_another_player = newProperty("player.too-close-to-another-player", "{prefix}<red>You are too close to a player that is opening a crate.");
}