package us.crazycrew.crazycrates.api.config.impl.types.editor;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import java.util.List;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newProperty;

public class EditorKeys implements IPropertyHolder {

    @Override
    public void registerComments(@NonNull final CommentsBuilder configuration) {
        configuration.setComment(List.of(
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                " ",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                " ",
                "Sounds: https://mudkipdev.github.io/minecraft-sound-explorer/"
        ), "editor");
    }

    @Comment("If set to true, this changes the behavior of /crazycrates editor by having locations replaced on right click.")
    public static final Property<Boolean> overwrite_old_crate_locations = newProperty(false, "editor", "overwrite-old-crate-locations");
}