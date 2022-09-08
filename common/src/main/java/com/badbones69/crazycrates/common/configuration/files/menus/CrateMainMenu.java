package com.badbones69.crazycrates.common.configuration.files.menus;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.util.ArrayList;
import java.util.List;

public class CrateMainMenu extends AbstractConfig {

    @Key("settings.crate-menu.title")
    @Comment("""
            The title of the menu.
            Available Placeholders: %crate%
            """)
    public static String CRATE_MENU_TITLE = "<red>Crate Browser</red> <dark_gray>:</dark_gray> <reset>%crate%";

    @Key("settings.crate-menu.size")
    @Comment("""
            Choose how large this menu should be.
            1, 2, 3, 4, 5 or 6 are the available options.
            Only works with the CHEST type gui.
            """)
    public static Integer CRATE_MENU_SIZE = 6;

    @Key("settings.crate-menu.type")
    @Comment("""
            Available types are CHEST, WORKBENCH, HOPPER< DISPENSER, BREWING
            Every type except CHEST ignores "crate-menu.size"
            """)
    public static String CRATE_MENU_TYPE = "CHEST";

    @Key("crate-menu-filler.toggle")
    @Comment("Whether you want the filler to be enabled.")
    public static Boolean CRATE_MENU_FILLER_TOGGLE = false;

    @Key("crate-menu-filler.item")
    @Comment("The item you want to fill it.")
    public static String CRATE_MENU_FILLER_ITEM = "BLACK_STAINED_GLASS_PANE";

    @Key("crate-menu-filler.name")
    @Comment("The name of the item.")
    public static String CRATE_MENU_FILLER_NAME = " ";

    @Key("crate-menu-filler.lore")
    @Comment("The lore of the item.")
    public static List<String> CRATE_MENU_FILLER_LORE = new ArrayList<>() {};
}