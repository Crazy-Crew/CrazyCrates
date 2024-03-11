package us.crazycrew.crazycrates.platform.impl.messages;

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

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> must_be_a_player = newProperty("player.requirements.must-be-player", "{prefix}&cYou must be a player to use this command.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> must_be_console_sender = newProperty("player.requirements.must-be-console-sender", "{prefix}&cYou must be using console to use this command.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> must_be_looking_at_block = newProperty("player.requirements.must-be-looking-at-block", "{prefix}&cYou must be looking at a block.");

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> not_online = newProperty("player.target-not-online", "{prefix}&c{player} &7is not online.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> same_player = newProperty("player.target-same-player", "{prefix}&cYou can''t use this command on yourself.");

    @Comment("A list of available placeholders: {prefix}")
    public static final Property<String> no_permission = newProperty("player.no-permission", "{prefix}&cYou do not have permission to use that command/menu!");

    @Comment("A list of available placeholders: {prefix}, {crate}")
    public static final Property<String> inventory_not_empty = newProperty("player.inventory-not-empty", "{prefix}&cInventory is not empty, Please make room before opening &6{crate}.");

    @Comment("A list of available placeholders: {prefix}, {amount}, {keytype}, {key}")
    public static final Property<String> obtaining_keys = newProperty("player.obtaining-keys", "{prefix}&7You have been given &6{amount} {key} &7key(s).");

    @Comment("A list of available placeholders: {prefix}, {player}")
    public static final Property<String> too_close_to_another_player = newProperty("player.too-close-to-another-player", "{prefix}&cYou are too close to a player that is opening a crate.");
}