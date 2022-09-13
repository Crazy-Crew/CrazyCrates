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

    @Key("settings.preview-buttons.menu-button.material")
    @Comment("The material the Main Menu button will be.")
    public static String CRATE_PREVIEW_MENU_BUTTON_MATERIAL = "COMPASS";

    @Key("settings.preview-buttons.menu-button.name")
    @Comment("The name of the Main Menu button.")
    public static String CRATE_PREVIEW_MENU_BUTTON_NAME = "<gray><bold>»»</bold></gray> <red><bold>Menu</bold></red> <gray><bold>««</bold></gray>";

    @Key("settings.preview-buttons.menu-button.lore")
    @Comment("The lore for the Main Menu button.")
    public static List<String> CRATE_PREVIEW_MENU_BUTTON_LORE = new ArrayList<>() {{
        add("<gray>Return to the crate browser.</gray>");
    }};

    @Key("settings.preview-buttons.next-button.material")
    @Comment("The material the Next button will be.")
    public static String CRATE_PREVIEW_NEXT_BUTTON_MATERIAL = "FEATHER";

    @Key("settings.preview-buttons.next-button.name")
    @Comment("The name of the Next button.")
    public static String CRATE_PREVIEW_NEXT_BUTTON_NAME = "<orange><bold>Next »</bold></orange>";

    @Key("settings.preview-buttons.next-button.lore")
    @Comment("The lore for the Next button.")
    public static List<String> CRATE_PREVIEW_NEXT_BUTTON_LORE = new ArrayList<>() {{
        add("<gray><bold>Page:</bold></gray> <blue>%page%</blue>");
    }};

    @Key("settings.preview-buttons.back-button.material")
    @Comment("The material the Back button will be.")
    public static String CRATE_PREVIEW_BACK_BUTTON_MATERIAL = "FEATHER";

    @Key("settings.preview-buttons.back-button.name")
    @Comment("The name of the Back button.")
    public static String CRATE_PREVIEW_BACK_BUTTON_NAME = "<orange><bold>« Back</bold></orange>";

    @Key("settings.preview-buttons.back-button.lore")
    @Comment("The lore for the back button.")
    public static List<String> CRATE_PREVIEW_BACK_BUTTON_LORE = new ArrayList<>() {{
        add("<gray><bold>Page:</bold></gray> <blue>%page%</blue>");
    }};
}