package com.badbones69.crazycrates.configs;

import us.crazycrew.crazycore.files.FileExtension;
import us.crazycrew.crazycore.files.FileManager;
import us.crazycrew.crazycore.files.annotations.Comment;
import us.crazycrew.crazycore.files.annotations.Header;
import us.crazycrew.crazycore.files.annotations.Path;
import us.crazycrew.crazycore.files.enums.FileType;
import java.util.ArrayList;
import java.util.List;

@Header("""
            Discord: https://discord.gg/crazycrew
            Github: https://github.com/Crazy-Crew
            
            Report Issues: https://github.com/Crazy-Crew/CrazyCrates/issues
            Request Features/Support: https://github.com/orgs/Crazy-Crew/discussions
            
            Legacy color codes such as &7,&c no longer work. You must use MiniMessage
            https://docs.advntr.dev/minimessage/format.html#color
            """)
public class ConfigSettings extends FileExtension {

    // Crate Settings
    @Path("crate-settings.crate-actions.log-to-file")
    @Comment({
            "Warning: The log file as is may cause your server to crash.",
            "It is recommended to clear it occasionally.",
            "",
            "Option will always be set to false by default."
    })
    public static boolean LOG_TO_FILE = false;

    @Path("crate-settings.crate-actions.log-to-console")
    @Comment("Whether you want to log crate actions to console or not.")
    public static boolean LOG_TO_CONSOLE = false;

    @Path("crate-settings.preview-menu.toggle")
    @Comment("Whether you want /crates to open a menu or not.")
    public static boolean PREVIEW_MENU_TOGGLE = true;

    @Path("crate-settings.preview-menu.name")
    @Comment("The title of /crates menu.")
    public static String PREVIEW_MENU_NAME = "<purple>Crazy Crates</purple>";

    @Path("crate-settings.preview-menu.size")
    @Comment("The size of /crates menu. | Must be a multiply of 9 so 9,18,27,36,45")
    public static int PREVIEW_MENU_SIZE = 45;

    // TODO() Make it an option in every crate file.
    @Path("crate-settings.knock-back")
    @Comment({
            "Whether you want crates to knock you back if you have no keys.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static boolean CRATE_KNOCK_BACK = true;

    @Path("crate-settings.keys.inventory-not-empty.give-virtual-keys-message")
    @Comment("Whether to notify the player they were given a virtual key when inventory is not empty.")
    public static boolean GIVE_VIRTUAL_KEYS_MESSAGE = true;

    @Path("crate-settings.keys.inventory-not-empty.give-virtual-keys")
    @Comment("Whether to give virtual keys to a player if inventory is not empty.")
    public static boolean GIVE_VIRTUAL_KEYS = true;

    // TODO() Make it an option in every crate file.

    @Path("crate-settings.keys.key-sound.toggle")
    @Comment({
            "Whether the sound should play or not.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static boolean KEY_SOUND_TOGGLE = false;

    @Path("crate-settings.keys.key-sound.name")
    @Comment("The sound that plays when a player tries to open a crate without keys.")
    public static String KEY_SOUND_NAME = "ENTITY_VILLAGER_NO";

    @Path("crate-settings.physical-accepts-virtual-keys")
    @Comment("Whether physical crates accept virtual keys or not.")
    public static boolean PHYSICAL_ACCEPTS_VIRTUAL = true;
    @Path("crate-settings.keys.physical-accepts-physical-keys")
    @Comment("Whether physical crates accept physical keys or not.")
    public static boolean PHYSICAL_ACCEPTS_PHYSICAL = true;
    @Path("crate-settings.keys.virtual-accepts-physical-keys")
    @Comment("Whether virtual crates accept physical keys or not.")
    public static boolean VIRTUAL_ACCEPTS_PHYSICAL_KEYS = true;

    // TODO() Make it in an option in quadcrate files.
    @Path("crate-settings.quad-crate.timer")
    @Comment({
            "How long a quad crate should be open?",
            "",
            "Warning: This option will be moved to be an option per crate.",
            "Moving this will allow each quad-crate to have different timers.",
    })
    public static int QUAD_CRATE_TIMER = 300;

    // Disabled Worlds.
    @Path("crate-settings.disabled-worlds.toggle")
    @Comment({
            "Whether you want to deny crate usage in X world or not.",
            "",
            "Warning: This will potentially be moved to be an option per crate for more specific control."
    })
    public static boolean DISABLED_WORLDS_TOGGLE = false;

    @Path("crate-settings.disabled-worlds.worlds")
    @Comment("The list of worlds to deny crate usage in.")
    public static List<String> DISABLED_WORLDS = new ArrayList<>() {{
        add("world_nether");
    }};

    // Gui Settings
    @Path("gui-settings.buttons.menu.item")
    @Comment("The material of the menu button.")
    public static String MENU_BUTTON_ITEM = "COMPASS";
    @Path("gui-settings.buttons.menu.name")
    @Comment("The name of the menu button.")
    public static String MENU_BUTTON_NAME = "<purple>»</purple> <red>Menu</red> <purple>«</purple>";
    @Path("gui-settings.buttons.menu.lore")
    @Comment("The lore of the menu button.")
    public static List<String> MENU_BUTTON_LORE = new ArrayList<>() {{
        add("<gray>Return to the menu.</gray>");
    }};

    @Path("gui-settings.buttons.next.item")
    @Comment("The material of the next button.")
    public static String NEXT_BUTTON_ITEM = "FEATHER";
    @Path("gui-settings.buttons.next.name")
    @Comment("The name of the next button.")
    public static String NEXT_BUTTON_NAME = "<gold>Next</gold> <gray>»</gray>";
    @Path("gui-settings.buttons.next.lore")
    @Comment("The lore of the next button.")
    public static List<String> NEXT_BUTTON_LORE = new ArrayList<>() {{
        add("<gray>Page:</gray> <blue>%page%</blue>");
    }};

    @Path("gui-settings.buttons.back.item")
    @Comment("The material of the back button.")
    public static String BACK_BUTTON_ITEM = "FEATHER";
    @Path("gui-settings.buttons.back.name")
    @Comment("The name of the back button.")
    public static String BACK_BUTTON_NAME = "<gray>«</gray> <gold>Back</gold>";
    @Path("gui-settings.buttons.back.lore")
    @Comment("The lore of the back button.")
    public static List<String> BACK_BUTTON_LORE = new ArrayList<>() {{
        add("<gray>Page:</gray> <blue>%page%</blue>");
    }};

    @Path("gui-settings.filler-items.toggle")
    @Comment("Whether to the menu should be filled with items or not.")
    public static boolean FILLER_ITEMS_TOGGLE = false;
    @Path("gui-settings.filler-items.item")
    @Comment("The item type you want to use.")
    public static String FILLER_ITEMS_ITEM = "BLACK_STAINED_GLASS_PANE";
    @Path("gui-settings.filler-items.name")
    @Comment("The name of the item.")
    public static String FILLER_ITEMS_NAME = " ";
    @Path("gui-settings.filler-items.lore")
    @Comment("The lore of the item.")
    public static List<String> FILLER_ITEMS_LORE = new ArrayList<>() {};

    @Path("gui-settings.customizer.toggle")
    @Comment("Whether you want the customizer to be enabled.")
    public static boolean CUSTOMIZER_TOGGLE = true;

    @Path("gui-settings.customizer.items")
    @Comment("Place any fancy item in the gui including custom items.")
    public static List<String> CUSTOMIZER = new ArrayList<>() {{
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

    public ConfigSettings() {
        super("config.yml", FileType.YAML);
    }

    public static void reload(FileManager fileManager) {
        fileManager.addFile(new ConfigSettings());
    }
}