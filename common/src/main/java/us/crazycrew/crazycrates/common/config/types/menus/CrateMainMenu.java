package us.crazycrew.crazycrates.common.config.types.menus;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

import java.util.List;

public class CrateMainMenu implements SettingsHolder {

    protected CrateMainMenu() {}

    @Comment("If the menu should be enabled or not.")
    public static final Property<Boolean> crate_menu_toggle = PropertyInitializer.newProperty("settings.crate-menu.toggle", true);

    @Comment({"""
            The title of /crates menu.
            Available Placeholders: {crate}
            """})
    public static final Property<String> crate_menu_title = PropertyInitializer.newProperty("settings.crate-menu.title","&cCrate Preview &8: &r{crate}");

    @Comment({"""
            Choose how large this menu should be.
            9, 18, 27, 36, or 45 are the available options.
            """})
    public static final Property<Integer> crate_menu_size = PropertyInitializer.newProperty("settings.crate-menu.size",45);

    //@Comment({"""
    //        Available types are CHEST, WORKBENCH, HOPPER, DISPENSER, BREWING
    //        Every type except CHEST ignores "crate-menu.size"
    //        """})
    //public static final Property<String> crate_menu_type = PropertyInitializer.newProperty("settings.crate-menu.type","CHEST");

    @Comment("Whether you want the filler to be enabled.")
    public static final Property<Boolean> crate_menu_filler_toggle = PropertyInitializer.newProperty("crate-menu-filler.toggle",false);

    @Comment("The item you want to fill it.")
    public static final Property<String> crate_menu_filler_item = PropertyInitializer.newProperty("crate-menu-filler.material","BLACK_STAINED_GLASS_PANE");

    @Comment("The name of the item.")
    public static final Property<String> crate_menu_filler_name = PropertyInitializer.newProperty("crate-menu-filler.name"," ");

    @Comment("The lore of the item.")
    public static final Property<List<String>> crate_menu_filler_lore = PropertyInitializer.newListProperty("crate-menu-filler.lore", "");

}