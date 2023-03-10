package com.badbones69.crazycrates.configs;

import us.crazycrew.crazycore.files.annotations.Comment;
import us.crazycrew.crazycore.files.annotations.Path;

public class PluginSettings {

    @Comment({
            "Choose what prefix you want to use for the permission i.e crazycrates.command.player.help",
            "",
            "Warning: any changes requires a restart!"
    })
    @Path("settings.permissions")
    public static final String COMMAND_PERMISSION = "crazycrates";

    @Comment("The command prefix that is shown at the beginning of every message.")
    @Path("settings.prefix.command")
    public static final String COMMAND_PREFIX = "<red>[</red><green>CrazyCrates</green><red>]</green> ";

    @Comment("The prefix that is shown for messages sent in console such as logging messages.")
    @Path("settings.prefix.console")
    public static final String CONSOLE_PREFIX = "<gradient:#fe5f55:#6b55b5>[CrazyCrates]</gradient> ";

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
    @Path("settings.locale-file")
    public static final String LOCALE_FILE = "en-US";

    @Comment("Whether you want to have verbose logging enabled or not.")
    @Path("settings.verbose-logging")
    public static final boolean VERBOSE_LOGGING = true;

    @Comment("Whether or not you would like to check for plugin updates on startup.")
    @Path("settings.update-checker")
    public static final boolean UPDATE_CHECKER = true;

    @Comment("Whether or not you would like to allow us to collect statistics on how our plugin is used.")
    @Path("settings.toggle.metrics")
    public static final boolean PLUGIN_METRICS = true;

    @Comment({
            "What command aliases do you want to use?",
            "You can use as many as you would like, Separate each command using : and do not use any spaces!",
            "",
            "Warning: any changes requires a restart!"
    })
    @Path("settings.plugin-aliases")
    public static final String PLUGIN_ALIASES = "crazycrates:crates";
}