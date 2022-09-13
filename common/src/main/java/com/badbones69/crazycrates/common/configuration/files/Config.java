package com.badbones69.crazycrates.common.configuration.files;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;

import java.util.ArrayList;
import java.util.List;

public class Config extends AbstractConfig {

    @Key("settings.language-file")
    @Comment("""
            The language file to use from the locale folder.
            Supported languages are Spanish(sp), Czech(cz) & English(en).""")
    public static String LANGUAGE_FILE = "locale-en.yml";

    @Key("settings.verbose")
    @Comment("Whether you want to have extra logging enabled.")
    public static boolean TOGGLE_VERBOSE = true;

    @Key("settings.toggle-metrics")
    @Comment("Whether you want metrics to be enabled.")
    public static boolean TOGGLE_METRICS = true;

    @Key("settings.data-storage.storage-method")
    @Comment("""
            How the plugin should store data
            
            - Your Options
            | Remote Database Types - You need to supply connection information.
             |» MySQL *NOT IMPLEMENTED*
             |» MariaDB ( Recommended over MySQL ) *NOT IMPLEMENTED*
              
            | Local Database Types
             |» H2 *NOT IMPLEMENTED*
             
            | Text File Based Storage
             |» JSON (.json files) *DEFAULT
            """)
    public static String STORAGE_METHOD = "JSON";

    @Key("settings.data-storage.data.address")
    @Comment("""
            Define the address / port for the database.
            » Default port is used for each one.
             MariaDB: 3306
            » If the port is different, Use "host:port"
            """)
    public static String STORAGE_ADDRESS = "localhost";

    @Key("settings.data-storage.data.database")
    @Comment("The name of the database to use *Must be created beforehand manually.")
    public static String STORAGE_DATABASE = "minecraft";

    @Key("settings.data-storage.data.username")
    @Comment("The user you use to connect to the database. *root is not recommended!")
    public static String STORAGE_USERNAME = "root";

    @Key("settings.data-storage.data.password")
    @Comment("The password.")
    public static String STORAGE_PASSWORD = "";

    @Key("settings.data-storage.data.table-prefix")
    @Comment("What should the name of the table be?")
    public static String STORAGE_TABLE_PREFIX = "crazycrates_";

    @Key("settings.data-storage.data.pool-settings.max-pool-size")
    public static int STORAGE_MAX_POOL_SIZE = 10;

    @Key("settings.data-storage.data.pool-settings.min-idle")
    public static int STORAGE_MIN_IDLE = 10;

    @Key("settings.data-storage.data.pool-settings.max-life-time")
    public static int STORAGE_MAX_LIFE_TIME = 1800000;

    @Key("settings.data-storage.data.pool-settings.keep-alive-time")
    public static int STORAGE_KEEP_ALIVE_TIME = 0;

    @Key("settings.data-storage.data.pool-settings.connection-timeout")
    public static int STORAGE_CONNECTION_TIMEOUT = 5000;

    @Key("settings.data-storage.data.pool-settings.properties.use-unicode")
    public static boolean STORAGE_USE_UNICODE = true;

    @Key("settings.data-storage.data.pool-settings.properties.character-encoding")
    public static String STORAGE_CHARACTER_ENCODING = "utf8";

    @Key("settings.crate.knock-back")
    @Comment("If crates should have knock back if they have no keys.")
    public static boolean TOGGLE_CRATE_KNOCKBACK = true;

    @Key("settings.crate.keys.physical-crate-accepts-virtual-keys")
    @Comment("Should a physical crate accept virtual keys?")
    public static boolean PHYSICAL_CRATE_ACCEPTS_VIRTUAL_KEYS = true;

    @Key("settings.crate.keys.virtual-crate-accepts-physical-keys")
    @Comment("Should a virtual crate ( /crates ) accept physical keys?")
    public static boolean VIRTUAL_CRATE_ACCEPTS_PHYSICAL_KEYS = true;

    @Key("settings.crate.give-virtual-keys-with-full-inventory")
    @Comment("Should I give virtual keys if inventory is full?")
    public static boolean GIVE_VIRTUAL_KEYS_WITH_FULL_INVENTORY = false;

    @Key("settings.crate.keys.key-sound.enabled")
    @Comment("Do you want me to play a sound?")
    public static boolean KEY_SOUND_ENABLED = false;

    @Key("settings.crate.keys.key-sound.name")
    @Comment("The sound you want to play!")
    public static String KEY_SOUND_NAME = "ENTITY_VILLAGER_NO";

    @Key("settings.crate.quad-crate.timer")
    @Comment("I don't know what this is yet.")
    public static long QUAD_CRATE_TIMERS = 300;

    @Key("settings.crate.disabled-worlds")
    @Comment("What worlds do you want Crates to be disabled in?")
    public static List<String> DISABLED_WORLDS = new ArrayList<>() {{
        add("world_nether");
    }};
}