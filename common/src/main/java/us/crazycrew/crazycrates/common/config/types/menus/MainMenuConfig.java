package us.crazycrew.crazycrates.common.config.types.menus;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;
import java.util.Collections;
import java.util.List;

public class MainMenuConfig implements SettingsHolder {

    protected MainMenuConfig() {}

    @Comment("If the menu should be enabled or not.")
    public static final Property<Boolean> crate_menu_toggle = PropertyInitializer.newProperty("settings.crate-menu.toggle", true);

    @Comment("The title of /crates menu.")
    public static final Property<String> crate_menu_title = PropertyInitializer.newProperty("settings.crate-menu.title","<red>Main Menu");

    @Comment({"""
            Choose how large this menu should be. 9 is how many rows a chest has.
            """})
    public static final Property<Integer> crate_menu_rows = PropertyInitializer.newProperty("settings.crate-menu.rows",3);

    @Comment({"""
            Available types are CHEST, WORKBENCH, HOPPER, DISPENSER, BREWING
            Every type except CHEST ignores "crate-menu.row"
            """})
    public static final Property<String> crate_menu_type = PropertyInitializer.newProperty("settings.crate-menu.type","CHEST");

    @Comment("Whether you want the filler to be enabled.")
    public static final Property<Boolean> crate_menu_filler_toggle = PropertyInitializer.newProperty("settings.filler.toggle",false);

    @Comment("The name of the item.")
    public static final Property<String> crate_menu_filler_name = PropertyInitializer.newProperty("settings.filler.name"," ");

    @Comment("The lore of the item.")
    public static final Property<List<String>> crate_menu_filler_lore = PropertyInitializer.newListProperty("settings.filler.lore", Collections.emptyList());

    @Comment("Whether you want to have a single item fill the menu or allow yourself to use the more configurable option below.")
    public static final Property<Boolean> crate_menu_filler_single_item = PropertyInitializer.newProperty("settings.filler.single-item.toggle", true);

    @Comment("The item you want to fill it. crate-menu-filler.single-item must be true.")
    public static final Property<String> crate_menu_filler_item = PropertyInitializer.newProperty("settings.filler.single-item.material","BLACK_STAINED_GLASS_PANE");

    @Comment("Per slot configuration for filler items. crate-menu-filler.single-item must be false.")
    public static final Property<List<String>> crate_menu_filler_items = PropertyInitializer.newListProperty("settings.filler.multiple-items", List.of(
            "row:1, column:1, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:1, column:2, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:1, column:3, item:GREEN_STAINED_GLASS_PANE, name: ",
            "row:1, column:4, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:1, column:5, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:1, column:6, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:1, column:7, item:RED_STAINED_GLASS_PANE, name: ",
            "row:1, column:8, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:1, column:9, item:BLACK_STAINED_GLASS_PANE, name: ",

            "row:2, column:1, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:2, column:2, item:GREEN_STAINED_GLASS_PANE, name: ",
            "row:2, column:4, item:GREEN_STAINED_GLASS_PANE, name: ",
            "row:2, column:5, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:2, column:6, item:RED_STAINED_GLASS_PANE, name: ",
            "row:2, column:8, item:RED_STAINED_GLASS_PANE, name: ",
            "row:2, column:9, item:BLACK_STAINED_GLASS_PANE, name: ",

            "row:3, column:1, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:3, column:2, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:3, column:3, item:GREEN_STAINED_GLASS_PANE, name: ",
            "row:3, column:4, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:3, column:5, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:3, column:6, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:3, column:7, item:RED_STAINED_GLASS_PANE, name: ",
            "row:3, column:8, item:BLACK_STAINED_GLASS_PANE, name: ",
            "row:3, column:9, item:BLACK_STAINED_GLASS_PANE, name: "));
}