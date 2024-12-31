package com.badbones69.crazycrates.core.config.impl;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class EditorKeys implements SettingsHolder {

    protected EditorKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "",
                "List of all sounds: https://minecraft.wiki/w/Sounds.json#Java_Edition_values",
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens. Please read the latest changelogs",
                ""
        };

        conf.setComment("editor", header);
    }

    @Comment("If this is set to true, when you are in /crazycrates editor... any existing crate locations will be overridden on right click.")
    public static final Property<Boolean> overwrite_old_crate_locations = newProperty("editor.overwrite-old-crate-locations", false);

}