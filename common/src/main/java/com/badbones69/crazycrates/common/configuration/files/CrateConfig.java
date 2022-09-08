package com.badbones69.crazycrates.common.configuration.files;

import com.badbones69.crazycrates.common.configuration.AbstractConfig;
import java.util.ArrayList;
import java.util.List;

public class CrateConfig extends AbstractConfig {

    @Key("crate.type")
    @Comment("""
            Available Crate Types : CSGO
            
            Make sure to check our wiki!
            https://github.com/Crazy-Crew/CrazyCrates/wiki
            """)
    public static String CRATE_TYPE = "CSGO";

    @Key("crate.name")
    @Comment("Name of the Crate if used in /crates.")
    public static String CRATE_NAME = "<green>Example Crate</green>";

    @Key("crate.lore")
    @Comment("""
            The lore of the Crate in /crates
            Available Placeholders: %keys%
            """)
    public static List<String> CRATE_LORE = new ArrayList<>() {{
        add("<gray>This crate contains strange objects.</gray>");
        add("<gray>You have <orange>%keys% keys</orange> <gray>to open this crate with.</gray>");
        add("<gray>(</gray><yellow>!</yellow><gray>)</gray> <yellow>Right click to view rewards</yellow>");
    }};

    @Key("crate.starting-keys")
    @Comment("The starting keys for when a player first joins.")
    public static Integer CRATE_STARTING_KEYS = 0;

    @Key("crate.in-menu")
    @Comment("""
            If the crate should show up in /crates menu
            """)
    public static Boolean CRATE_IN_MENU = true;

    @Key("crate.slot")
    @Comment("The slot of the item in the menu.")
    public static Integer CRATE_ITEM_SLOT = 21;

    @Key("crate.opening-broadcast")
    @Comment("If a message should be sent when a crate is open.")
    public static Boolean CRATE_OPENING_BROADCAST = true;

    @Key("crate.broadcast-message")
    @Comment("""
            The message sent when opening the crate.
            Available Placeholders: %player%, %crate%
            """)
    public static String CRATE_BROADCAST_MESSAGE = "<orange><bold>^player%</bold></orange> <gray>is opening a</gray> <reset>%crate%</reset>";

    @Key("crate.item")
    @Comment("The material of the crate in the menu.")
    public static String CRATE_ITEM = "CHEST";

    @Key("crate.glowing")
    @Comment("If the item in the main menu is glowing or not.")
    public static Boolean CRATE_GLOWING = false;

    /**
     * Crate Preview Settings.
     */
    @Key("crate.preview.toggle")
    @Comment("Turn the preview for this Crate on & off.")
    public static Boolean CRATE_PREVIEW_TOGGLE = true;

    @Key("crate.preview.size")
    @Comment("""
            How large should the crate preview be?
            """)
    public static Integer CRATE_PREVIEW_SIZE = 6;

    @Key("crate.preview-name")
    @Comment("The name of the Preview Crate when right clicking a crate.")
    public static String CRATE_PREVIEW_NAME = "<green>Example Crate Preview</green>";

    @Key("crate.preview.border.toggle")
    @Comment("If the glass border should be enabled or not.")
    public static Boolean CRATE_PREVIEW_BORDER_TOGGLE = true;

    @Key("crate.preview.border.item")
    @Comment("The item that will show in the border. GRAY_STAINED_GLASS_PANE is default.")
    public static String CRATE_PREVIEW_BORDER_ITEM = "GRAY_STAINED_GLASS_PANE";

    /**
     * Physical Key Settings.
     */
    @Key("crate.physical-key.name")
    @Comment("The name of the Crate Key!")
    public static String CRATE_PHYSICAL_KEY_NAME = "<gray>Example Crate Key</gray>";

    @Key("crate.physical-key.lore")
    @Comment("The lore of the Crate Key!")
    public static List<String> CRATE_PHYSICAL_KEY_LORE = new ArrayList<>() {{
        add("<gray>A strange mystical key</gray>");
        add("<gray>which opens a strange crate</gray>");
    }};

    @Key("crate.physical-key.item")
    @Comment("""
            The item that represents your Crate Key!
            
            and yes for the 100th time, We support Custom Model Data
            TRIPWIRE_HOOK#1 is an example of how to do it.
            """)
    public static String CRATE_PHYSICAL_KEY_ITEM = "TRIPWIRE_HOOK";

    @Key("crate.physical-key.glowing")
    @Comment("If the key is glowing or not.")
    public static Boolean CRATE_PHYSICAL_KEY_ITEM_GLOWING = true;

    /**
     * Hologram Settings
     */
    @Key("crate.hologram.toggle")
    @Comment("Whether holograms should be enabled for the crate.")
    public static Boolean CRATE_HOLOGRAM_TOGGLE = true;

    @Key("crate.hologram.height")
    @Comment("The height of the hologram above your crate.")
    public static Double CRATE_HOLOGRAM_HEIGHT = 1.5;

    @Key("crate.hologram.message")
    @Comment("The entire message in your hologram.")
    public static List<String> CRATE_HOLOGRAM_LORE = new ArrayList<>() {{
       add("<orange>An example hologram</orange>");
    }};
}