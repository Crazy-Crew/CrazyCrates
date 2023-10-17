package us.crazycrew.crazycrates.common.config.types;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

public class PluginConfig implements SettingsHolder {

    protected PluginConfig() {}

    @Comment({
            "Choose the language you prefer to use on your server!",
            "",
            "Currently Available:",
            " > en-US ( English )",
            "",
            "If you do not see your language above, You can contribute by modifying the current en-US.yml",
            "https://github.com/Crazy-Crew/CrazyCrates/blob/main/paper/src/main/resources/locale/en-US.yml",
            "Submit your finalized config using https://bin.bloom.host/ and send it to us in https://discord.gg/badbones-s-live-chat-182615261403283459",
            ""
    })
    public static final Property<String> locale_file = PropertyInitializer.newProperty("plugin_locale", "en-US");

    @Comment("Whether you want CrazyCrates to shut up or not, This option is ignored by errors.")
    public static final Property<Boolean> verbose_logging = PropertyInitializer.newProperty("verbose_logging", true);

    @Comment({
            "Sends anonymous statistics about how the plugin is used to bstats.org.",
            "bstats is a service for plugin developers to find out how the plugin being used,",
            "This information helps us figure out how to better improve the plugin."
    })
    public static final Property<Boolean> toggle_metrics = PropertyInitializer.newProperty("toggle_metrics", true);

    // black, dark_blue, dark_green, dark_aqua, dark_red, dark_purple, gold, gray, dark_gray, blue, green, aqua, red, light_purple, yellow, or white
    @Comment("The command prefix you want shown in front of commands!")
    public static final Property<String> command_prefix = PropertyInitializer.newProperty("command_prefix", "<dark_gray>[<blue>CrazyCrates<dark_gray>] <reset>");

}