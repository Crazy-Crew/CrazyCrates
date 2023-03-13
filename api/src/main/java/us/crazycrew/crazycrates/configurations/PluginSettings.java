package us.crazycrew.crazycrates.configurations;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Description: The plugin-settings.yml options.
 */
public class PluginSettings implements SettingsHolder {

    // Empty constructor required by SettingsHolder
    public PluginSettings() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/crazycrew",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/discussions"
        };

        conf.setComment("settings", header);
    }

    @Comment({
            "Choose what prefix you want to use for the permission i.e crazycrates.command.player.help",
            "",
            "Warning: any changes requires a restart!"
    })
    public static final Property<String> COMMAND_PERMISSION = newProperty("settings.permission", "crazycrates");

    public static final Property<Boolean> COMMAND_PREFIX_TOGGLE = newProperty("settings.prefix.command.enabled", true);

    @Comment("The command prefix that is shown at the beginning of every message.")
    public static final Property<String> COMMAND_PREFIX = newProperty("settings.prefix.command.value", "<red>[</red><green>CrazyCrates</green><red>]</green> ");

    @Comment("The prefix that is shown for messages sent in console such as logging messages.")
    public static final Property<String> CONSOLE_PREFIX = newProperty("settings.prefix.console", "<gradient:#fe5f55:#6b55b5>[CrazyCrates]</gradient> ");

    @Comment({
            "Choose the language you prefer to use on your server!",
            "",
            "Currently Available:",
            " > en-US ( English )",
            "",
            "If you do not see your language above, You can contribute by modifying the current en-US.yml",
            "https://github.com/Crazy-Crew/CrazyCrates/blob/main/platforms/paper/src/main/resources/locale/en-US.yml",
            "Submit your finalized config using https://bin.bloom.host/ and send it to us in https://discord.gg/crazycrew",
            ""
    })
    public static final Property<String> LOCALE_FILE = newProperty("settings.locale-file", "en-US");

    @Comment("Whether you want to have verbose logging enabled or not.")
    public static final Property<Boolean> VERBOSE_LOGGING = newProperty("settings.verbose-logging", true);

    @Comment("Whether or not you would like to check for plugin updates on startup.")
    public static final Property<Boolean> UPDATE_CHECKER = newProperty("settings.update-checker", true);

    @Comment("Whether or not you would like to allow us to collect statistics on how our plugin is used.")
    public static final Property<Boolean> PLUGIN_METRICS = newProperty("settings.toggle-metrics", true);

    @Comment({
            "What command aliases do you want to use?",
            "You can use as many as you would like, Separate each command using : and do not use any spaces!",
            "",
            "Warning: any changes requires a restart!"
    })
    public static final Property<String> PLUGIN_ALIASES = newProperty("settings.plugin-aliases", "crazycrates:crates");
}