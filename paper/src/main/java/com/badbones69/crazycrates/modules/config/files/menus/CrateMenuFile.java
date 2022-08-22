package com.badbones69.crazycrates.modules.config.files.menus;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import dev.triumphteam.gui.components.GuiType;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CrateMenuFile extends AbstractConfig {

    private static final CrateMenuFile CRATE_MENU_FILE = new CrateMenuFile();

    // Inventory Size
    public static Integer INVENTORY_SIZE = 45;

    // Inventory Type
    public static GuiType INVENTORY_TYPE = GuiType.CHEST;

    // Filler Toggle
    public static Boolean FILLER_TOGGLE = false;

    // Filler Item
    public static String FILLER_ITEM = "BLACK_STAINED_GLASS_PANE";

    // Filler Name
    public static String FILLER_NAME = " ";

    // Filler Lore
    public static List<String> FILLER_LIST = new ArrayList<>() {};

    // Gui Customizer
    public static List<String> GUI_CUSTOMIZER = new ArrayList<>() {{
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
        add("Slot:32, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:33, Item:WHITE_STAINED_GLASS_PANE, Name: ");

        add("Slot:34, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:35, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:38, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:39, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:40, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:41, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:42, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:43, Item:WHITE_STAINED_GLASS_PANE, Name: ");
        add("Slot:44, Item:WHITE_STAINED_GLASS_PANE, Name: ");
    }};

    public static void reload(Path path, CrazyCrates plugin, CrazyLogger logger) {
        CRATE_MENU_FILE.handle(path.resolve("crate-menu.yml"), CrateMenuFile.class, plugin, logger);
    }
}