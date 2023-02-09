package com.badbones69.crazycrates.configs;

import com.badbones69.crazycrates.CrazyCrates;
import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.annotations.yaml.BlockType;
import net.dehya.ruby.common.annotations.yaml.Comment;
import net.dehya.ruby.common.annotations.yaml.Header;
import net.dehya.ruby.common.annotations.yaml.Key;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@FileBuilder(isLogging = true, isAsync = false, isData = false, fileType = FileType.YAML)
@Header("""
            Discord: https://discord.gg/crazycrew
            Github: https://github.com/Crazy-Crew
            
            Report Issues: https://github.com/Crazy-Crew/CrazyCrates/issues
            Request Features/Support: https://github.com/orgs/Crazy-Crew/discussions
            """)
public class Config extends FileExtension {

    @Key("settings.prefix")
    @Comment("The prefix used in front of messages.")
    public static String PREFIX = "&8[&bCrazyCrates&8]: ";

    @Key("settings.locale-file")
    @Comment("""
            The language file to use from the locale folder.
            
            Supported languages are Spanish(sp), Czech(cz), English(en).
            """)
    @BlockType
    public static String LOCALE_FILE = "locale-en.yml";

    @Key("settings.update-checker")
    @Comment("Whether you want to be notified when an update is published to Modrinth.")
    public static boolean UPDATE_CHECKER = true;

    @Key("settings.toggle-metrics")
    @Comment("Whether you want your server statistics to be sent to https://bstats.org/ ( Requires a restart! )")
    public static boolean TOGGLE_METRICS = true;

    @Key("settings.config-version")
    @Comment("DO NOT TOUCH THIS: We use this to identify if the config is outdated.")
    public static double CONFIG_VERSION = 1.1;

    @Key("settings.use-mini-message")
    @Comment("""
            WARNING: If you switch this option to "true", &7 for example will no longer work.
            
            You will have to use https://docs.adventure.kyori.net/minimessage/format.html
            NOTE: This option is a temporary courtesy, &7 at a point might be completely removed.
            
            Example of how to use MiniMessage: <gray>Hello <gold>How are you?</gold></gray>
            """)
    @BlockType
    public static boolean USE_MINIMESSAGE = false;

    // Crate Settings
    @Key("crate-settings.crate-actions.log-to-file")
    @Comment("""
            Warning: The log file may cause your server to crash.
            It is recommended you clear it occasionally.
            
            Option will always be set to false by default.
            """)
    @BlockType
    public static boolean LOG_TO_FILE = false;

    @Key("crate-settings.crate-actions.log-to-console")
    @Comment("Whether you want to log to console or not.")
    public static boolean LOG_TO_CONSOLE = false;

    @Key("crate-settings.preview-menu.toggle")
    @Comment("Whether you want /crates to open a menu or not.")
    public static boolean PREVIEW_MENU_TOGGLE = true;
    @Key("crate-settings.preview-menu.name")
    @Comment("The title of the /crates menu.")
    public static String PREVIEW_MENU_NAME = "&d&lCrazy &5&lCrates";
    @Key("crate-settings.preview-menu.size")
    @Comment("The size of the /crates menu.")
    public static int PREVIEW_MENU_SIZE = 45;

    // TODO() Make it an option in every crate file.
    @Key("crate-settings.knock-back")
    @Comment("""
            Whether you want crates to knock you back if you have no keys.
            
            Warning: This will be moved to be an option per crate.
            """)
    @BlockType
    public static boolean CRATE_KNOCK_BACK = true;

    @Key("crate-settings.keys.inventory-not-empty.give-virtual-keys-message")
    @Comment("Whether to notify the player they were given a virtual key when inventory is not empty.")
    public static boolean GIVE_VIRTUAL_KEYS_MESSAGE = true;

    @Key("crate-settings.keys.inventory-not-empty.give-virtual-keys")
    @Comment("Whether to give virtual keys to a player if inventory is not empty.")
    public static boolean GIVE_VIRTUAL_KEYS = true;

    // TODO() Make it an option in every crate file.

    @Key("crate-settings.keys.key-sound.toggle")
    @Comment("""
            Whether the sound should play or not.
            
            Warning: This will be moved to be an option per crate.
            """)
    @BlockType
    public static boolean KEY_SOUND_TOGGLE = false;

    @Key("crate-settings.keys.key-sound.name")
    @Comment("This sound will play when a player tries to open a crate without a virtual/physical key.")
    public static String KEY_SOUND_NAME = "ENTITY_VILLAGER_NO";

    @Key("crate-settings.physical-accepts-virtual-keys")
    @Comment("Whether physical crates accept virtual keys or not.")
    public static boolean PHYSICAL_ACCEPTS_VIRTUAL = true;
    @Key("crate-settings.keys.physical-accepts-physical-keys")
    @Comment("Whether physical crates accept physical keys or not.")
    public static boolean PHYSICAL_ACCEPTS_PHYSICAL = true;
    @Key("crate-settings.keys.virtual-accepts-physical-keys")
    @Comment("Whether virtual crates accept physical keys or not.")
    public static boolean VIRTUAL_ACCEPTS_PHYSICAL_KEYS = true;

    // TODO() Make it in an option in quadcrate files.
    @Key("crate-settings.quad-crate.timer")
    @Comment("""
            How long a quad crate should be open?
            
            Warning: This will be moved to be an option in QuadCrate files.
            Moving this will allow each quadcrate to have different timers.
            """)
    @BlockType
    public static int QUAD_CRATE_TIMER = 300;

    // Disabled Worlds.
    @Key("crate-settings.disabled-worlds.toggle")
    @Comment("""
            Whether you want to deny crate usage in X world or not.
            
            Warning: This will potentially be moved to be an option per crate for more specific control.
            """)
    @BlockType
    public static boolean DISABLED_WORLDS_TOGGLE = false;

    @Key("crate-settings.disabled-worlds.worlds")
    @Comment("The list of worlds to deny crate usage in.")
    public static List<String> DISABLED_WORLDS = new ArrayList<>() {{
        add("world_nether");
    }};

    // Gui Settings
    @Key("gui-settings.buttons.menu.item")
    @Comment("The material of the Menu Button.")
    public static String MENU_BUTTON_ITEM = "COMPASS";
    @Key("gui-settings.buttons.menu.name")
    @Comment("The name of the Menu Button.")
    public static String MENU_BUTTON_NAME = "&d&l>> &c&lMenu &d&l<<";
    @Key("gui-settings.buttons.menu.lore")
    @Comment("The lore of the Menu Button.")
    public static List<String> MENU_BUTTON_LORE = new ArrayList<>() {{
        add("&7Return to the menu.");
    }};

    @Key("gui-settings.buttons.next.item")
    @Comment("The material of the Next Button.")
    public static String NEXT_BUTTON_ITEM = "FEATHER";
    @Key("gui-settings.buttons.next.name")
    @Comment("The name of the Next Button.")
    public static String NEXT_BUTTON_NAME = "&6&lNext >>";
    @Key("gui-settings.buttons.next.lore")
    @Comment("The lore of the Next Button.")
    public static List<String> NEXT_BUTTON_LORE = new ArrayList<>() {{
        add("&7&lPage: &b%page%");
    }};

    @Key("gui-settings.buttons.back.item")
    @Comment("The material of the Back Button.")
    public static String BACK_BUTTON_ITEM = "FEATHER";
    @Key("gui-settings.buttons.back.name")
    @Comment("The name of the Back Button.")
    public static String BACK_BUTTON_NAME = "&6&l<< Back";
    @Key("gui-settings.buttons.back.lore")
    @Comment("The lore of the Back Button.")
    public static List<String> BACK_BUTTON_LORE = new ArrayList<>() {{
        add("&7&lPage: &b%page%");
    }};

    @Key("gui-settings.filler-items.toggle")
    @Comment("""
            Whether to fill the gui with an item.
            
            Warning: This entire section from here on including gui-customizer is subject to change.
            """)
    @BlockType
    public static boolean FILLER_ITEMS_TOGGLE = false;
    @Key("gui-settings.filler-items.item")
    @Comment("The item you wish to use.")
    public static String FILLER_ITEMS_ITEM = "BLACK_STAINED_GLASS_PANE";
    @Key("gui-settings.filler-items.name")
    @Comment("The name of the item.")
    public static String FILLER_ITEMS_NAME = " ";
    @Key("gui-settings.filler-items.lore")
    @Comment("The lore of the item.")
    public static List<String> FILLER_ITEMS_LORE = new ArrayList<>() {};

    @Key("gui-settings.customizer")
    @Comment("""
            Place extra items in the gui, If you simply wish to not use this.
            Set this to customizer: []
            """)
    @BlockType
    public static List<String> FILLER_EXTRA_ITEMS = new ArrayList<>() {{
        // Black Glass.
        add("Slot:1, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:2, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:3, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:4, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:5, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:6, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:7, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:8, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:9, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:10, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:18, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:19, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:27, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:28, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:36, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:37, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:45, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        
        // White Glass
        add("Slot:11, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:12, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:13, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:14, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:15, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:16, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:17, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:20, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:22, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:24, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:30, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:33, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:34, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:35, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        
        // Black Glass
        add("Slot:38, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:39, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:40, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:41, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:42, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:43, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:44, Item:BLACK_STAINED_GLASS_PANE, Name: ");
    }};

    public Config() {
        super("config.yml");
    }

    public static void reload(CrazyCrates plugin) {
        plugin.getPaperManager().getPaperFileManager().addFile(new Config());
    }

    public static YamlConfiguration getConfiguration(CrazyCrates plugin) {
        return plugin.getPaperManager().getPaperFileManager().getFileConfiguration(new Config());
    }

    public static File getConfig(CrazyCrates plugin) {
        return plugin.getPaperManager().getPaperFileManager().getFile(new Config());
    }
}