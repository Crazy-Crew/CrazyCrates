package com.badbones69.crazycrates.config;

import com.badbones69.crazycrates.api.files.AbstractConfig;
import com.badbones69.crazycrates.api.files.annotations.Comment;
import com.badbones69.crazycrates.api.files.annotations.Key;
import com.badbones69.crazycrates.utils.FileUtils;
import java.util.*;

public class Config extends AbstractConfig {

    @Key("Settings.Prefix")
    @Comment("The base prefix for the plugin.")
    public static String prefix = "<dark_gray>[</dark_gray><gradient:#5e4fa2:#f79459>CrazyCrates</gradient><dark_gray]</dark_gray>: ";

    @Key("Settings.Toggle-Metrics")
    @Comment("Turn metrics on or off.")
    public static boolean toggleMetrics = true;

    @Key("Settings.Language-File")
    @Comment("Check the locale directory for a list of available languages.")
    public static String languageFile = "locale-en.yml";

    @Key("Settings.Extra-Logging")
    @Comment("Turning this on is useful for debugging but gets spammy very quickly.")
    public static boolean extraLogging = true;

    @Key("Settings.InventoryName")
    public static String inventoryName = "<blue><bold>Crazy</blue> <red>Crates</red></bold>";

    @Key("Settings.InventorySize")
    public static int inventorySize = 45;

    @Key("Settings.KnockBack")
    public static boolean knockBack = true;

    @Key("Settings.Physical-Accepts-Virtual-Keys")
    public static boolean physicalAcceptsVirtualKeys = true;

    @Key("Settings.Virtual-Accepts-Physical-Keys")
    public static boolean virtualAcceptsPhysicalKeys = true;

    @Key("Settings.Give-Virtual-Keys-When-Inventory-Full")
    public static boolean giveVirtualKeysWhenInventoryFull = false;

    @Key("Settings.Need-Key-Sound")
    public static String needKeySound = "ENTITY_VILLAGER_NO";

    @Key("Settings.QuadCrate.Timer")
    public static int quadCrateTimer = 300;

    @Key("Settings.DisabledWorlds")
    public static List<String> disabledWorlds = new ArrayList<>() {{
        add("world_nether");
    }};

    @Key("Settings.Preview.Buttons.Menu.Item")
    public static String previewMenuItem = "COMPASS";

    @Key("Settings.Preview.Buttons.Menu.Name")
    public static String previewMenuName = "<bold><gray>!!!</gray> <red>Menu</red> <gray>!!!</gray></bold>";

    @Key("Settings.Preview.Buttons.Menu.Lore")
    public static List<String> previewMenuLore = new ArrayList<>() {{
        add("<gray>Return to the menu.</gray>");
    }};

    @Key("Settings.Preview.Buttons.Next.Item")
    public static String previewNextItem = "FEATHER";

    @Key("Settings.Preview.Buttons.Next.Name")
    public static String previewNextName = "<bold><gold>Next >></gold></bold>";

    @Key("Settings.Preview.Buttons.Next.Lore")
    public static List<String> previewNextLore = new ArrayList<>() {{
        add("<bold><gray>Page:</gray></bold> <blue>%page%</blue>");
    }};

    @Key("Settings.Preview.Buttons.Back.Item")
    public static String previewBackItem = "FEATHER";

    @Key("Settings.Preview.Buttons.Back.Name")
    public static String previewBackName = "&6&l<< Back";

    @Key("Settings.Preview.Buttons.Back.Lore")
    public static List<String> previewBackLore = new ArrayList<>() {{
        add("<bold><gray>Page:</gray></bold> <blue>%page%</blue>");
    }};

    @Key("Settings.Filler.Toggle")
    @Comment("If it fills the GUI with an item.")
    public static boolean fillerToggle = false;

    @Key("Settings.Preview.Buttons.Back.Lore")
    @Comment("The item you wish to use.")
    public static String fillerItem = "BLACK_STAINED_GLASS_PANE";

    @Key("Settings.Preview.Buttons.Back.Lore")
    @Comment("The name of the item.")
    public static String fillerName = " ";

    @Key("Settings.Filler.Lore")
    @Comment("The lore of the item.")
    public static List<String> fillerLore = new ArrayList<>() {};

    @Key("Settings.Filler.Lore")
    @Comment("Place extra items in the GUI. If you wish to not use this, remove all items and set it to GUI-Customizer: {}")
    public static List<String> guiCustomizer = new ArrayList<>() {{
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
        add("Slot:26, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:29, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:30, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:31, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:32, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:33, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:34, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:35, Item:WHITE_STAINED_GLASS_PANE, Name: ");

        add("Slot:38, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:39, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:40, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:41, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:42, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:43, Item:BLACK_STAINED_GLASS_PANE, Name: ");
        add("Slot:44, Item:BLACK_STAINED_GLASS_PANE, Name: ");
    }};

    private static final Config CONFIG = new Config();

    public static void reload() {
        CONFIG.reload(FileUtils.INSTANCE.getDataFolder().resolve("config.yml"), Config.class);
    }
}