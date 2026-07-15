package us.crazycrew.crazycrates.api.config.impl.types.config;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import us.crazycrew.crazycrates.api.enums.messages.State;
import java.util.List;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newBeanProperty;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newProperty;

public class RootKeys implements IPropertyHolder {

    @Override
    public void registerComments(@NonNull CommentsBuilder configuration) {
        final List<String> deprecation = List.of(
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens. Please read the latest changelogs",
                ""
        );

        configuration.setComment(List.of(
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "",
                "Sounds: https://mudkipdev.github.io/minecraft-sound-explorer/"
        ), "root");
    }

    @Comment("This will wipe the example folder on /crazycrates reload or plugin startup. so you always have fresh examples to look at.")
    public static final Property<Boolean> is_update_examples_folder = newProperty(true, "root", "update-examples-folder");

    @Comment("Sends anonymous statistics to https://bstats.org/plugin/bukkit/CrazyCrates/4514")
    public static final Property<Boolean> is_metrics_enabled = newProperty(true, "root", "toggle-metrics");

    @Comment({
            "This option will tell the plugin to send all messages as action bars or messages in chat.",
            "",
            "send_message -> sends messages in chat.",
            "send_actionbar -> sends messages in actionbar.",
            ""
    })
    public static final Property<State> get_message_state = newBeanProperty(State.class, State.send_message, "root", "message-state");

    @Comment("The prefix used in commands")
    public static final Property<String> get_command_prefix = newProperty(" <gradient:#e91e63:blue>CrazyCrates</gradient> | ", "root", "command_prefix");

    @Comment({
            "A list of available hologram plugins:",
            " -> DecentHolograms",
            " -> FancyHolograms",
            " -> CMI",
            " -> None",
            "",
            "If the option is set to blank, it'll pick whatever plugin it feels like picking.",
            "Set the value to None if you do not want any."
    })
    public static final Property<String> get_hologram_plugin = newProperty("", "root", "hologram-plugin");
}