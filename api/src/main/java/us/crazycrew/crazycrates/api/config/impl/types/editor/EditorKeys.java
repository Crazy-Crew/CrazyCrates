package us.crazycrew.crazycrates.api.config.impl.types.editor;

import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newProperty;

public class EditorKeys implements IPropertyHolder {

    /*@Override
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
    }*/

    //@Comment("If this is set to true, when you are in /crazycrates editor... any existing crate locations will be overridden on right click.")
    public static final Property<Boolean> overwrite_old_crate_locations = newProperty(false, "editor", "overwrite-old-crate-locations");
}