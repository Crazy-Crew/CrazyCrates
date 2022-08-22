package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import com.badbones69.crazycrates.utils.keys.Comment;
import com.badbones69.crazycrates.utils.keys.Key;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigFile extends AbstractConfig {

    @Key("settings.language-file")
    @Comment("""
            The language file to use from the locale folder.
            Supported languages are Spanish(sp), Czech(cz) & English(en).""")
    public static String LANGUAGE_FILE = "locale-en.yml";

    @Key("settings.toggle-metrics")
    @Comment("Whether you want metrics to be enabled.")
    public static Boolean TOGGLE_METRICS = true;

    @Key("settings.crate.knock-back")
    @Comment("If crates should have knock back if they have no keys.")
    public static Boolean TOGGLE_CRATE_KNOCKBACK = true;

    @Key("settings.crate.keys.physical-crate-accepts-virtual-keys")
    @Comment("Should a physical crate accept virtual keys?")
    public static Boolean PHYSICAL_CRATE_ACCEPTS_VIRTUAL_KEYS = true;

    @Key("settings.crate.keys.virtual-crate-accepts-physical-keys")
    @Comment("Should a virtual crate ( /crates ) accept physical keys?")
    public static Boolean VIRTUAL_CRATE_ACCEPTS_PHYSICAL_KEYS = true;

    @Key("settings.crate.give-virtual-keys-with-full-inventory")
    @Comment("Should I give virtual keys if inventory is full?")
    public static Boolean GIVE_VIRTUAL_KEYS_WITH_FULL_INVENTORY = false;

    @Key("settings.crate.keys.key-sound.enabled")
    @Comment("Do you want me to play a sound?")
    public static Boolean KEY_SOUND_ENABLED = false;

    @Key("settings.crate.keys.key-sound.name")
    @Comment("The sound you want to play!")
    public static String KEY_SOUND_NAME = "ENTITY_VILLAGER_NO";

    @Key("settings.crate.quad-crate.timer")
    @Comment("I don't know what this is yet.")
    public static Integer QUAD_CRATE_TIMERS = 300;

    @Key("settings.crate.disabled-worlds")
    @Comment("What worlds do you want Crates to be disabled in?")
    public static List<String> DISABLED_WORLDS = new ArrayList<>() {{
        add("world_nether");
    }};

    private static final ConfigFile CONFIG_FILE = new ConfigFile();

    public static void reload(Path path, CrazyCrates plugin, CrazyLogger logger) {
        CONFIG_FILE.handle(path.resolve("config.yml"), ConfigFile.class, plugin, logger);
    }
}