package com.badbones69.crazycrates.modules.config.files.menus;

import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import dev.triumphteam.gui.components.GuiType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrateMenuConfig extends AbstractConfig {

    @Key("settings.crate-menu-title")
    @Comment("""
            The title of the menu.
            Available Placeholders: %crate%
            """)
    public static String CRATE_MENU_TITLE = "<red>Crate Browser</red> <dark_gray>:</dark_gray> <reset>%crate%";

    @Key("settings.crate-menu-size")
    @Comment("""
            Choose how large this menu should be.
            1, 2, 3, 4, 5 or 6 are the available options.
            Only works with the CHEST type gui.
            """)
    public static Integer CRATE_MENU_SIZE = 6;

    @Key("settings.crate-menu-type")
    @Comment("""
            Available types are CHEST, WORKBENCH, HOPPER< DISPENSER, BREWING
            Every type except CHEST ignores "crate-menu-size"
            """)
    public static GuiType CRATE_MENU_TYPE = GuiType.CHEST;

    @Key("crate-menu-filler.toggle")
    public static Boolean CRATE_MENU_FILLER_TOGGLE = false;

    @Key("crate-menu-filler.item")
    public static String CRATE_MENU_FILLER_ITEM = "BLACK_STAINED_GLASS_PANE";

    @Key("crate-menu-filler.name")
    public static String CRATE_MENU_FILLER_NAME = " ";

    @Key("crate-menu-filler.lore")
    public static List<String> CRATE_MENU_FILLER_LORE = new ArrayList<>() {};

    private static final CrateMenuConfig CRATE_MENU_FILE = new CrateMenuConfig();

    public static void reload(Path path, CrazyLogger logger) {
        CRATE_MENU_FILE.handle(path.resolve("crate-menu.yml"), CrateMenuConfig.class, logger);
    }
}