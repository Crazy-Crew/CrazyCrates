package com.badbones69.crazycrates.common.configuration.files.menus;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.util.ArrayList;
import java.util.List;

public class CratePreviewMenu extends AbstractConfig {

    @Key("settings.crate-preview.title")
    @Comment("""
            The title of the menu.
            Available Placeholders: %crate%
            """)
    public static String CRATE_PREVIEW_TITLE = "<red>Crate Preview</red> <dark_gray>:</dark_gray> <reset>%crate%</reset>";

    public static String CRATE_PREVIEW_MENU_BUTTON_ITEM = "COMPASS";

    public static String CRATE_PREVIEW_MENU_BUTTON_NAME = "<gray><bold>»»</bold></gray> <red><bold>Menu</bold></red> <gray><bold>««</bold></gray>";

    public static List<String> CRATE_PREVIEW_MENU_BUTTON_LORE = new ArrayList<>() {{
        add("<gray>Return to the crate browser.</gray>");
    }};

    public static String CRATE_PREVIEW_NEXT_BUTTON_ITEM = "FEATHER";

    public static String CRATE_PREVIEW_NEXT_BUTTON_NAME = "<orange><bold>Next »</bold></orange>";

    public static List<String> CRATE_PREVIEW_NEXT_BUTTON_LORE = new ArrayList<>() {{
        add("<gray><bold>Page:</bold></gray> <blue>%page%</blue>");
    }};

    public static String CRATE_PREVIEW_BACK_BUTTON_ITEM = "FEATHER";

    public static String CRATE_PREVIEW_BACK_BUTTON_NAME = "<orange><bold>« Back</bold></orange>";

    public static List<String> CRATE_PREVIEW_BACK_BUTTON_LORE = new ArrayList<>() {{
        add("<gray><bold>Page:</bold></gray> <blue>%page%</blue>");
    }};
}