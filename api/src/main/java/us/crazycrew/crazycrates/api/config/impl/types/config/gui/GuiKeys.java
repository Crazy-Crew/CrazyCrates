package us.crazycrew.crazycrates.api.config.impl.types.config.gui;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.annotations.Alias;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.properties.builders.AliasBuilder;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import java.util.List;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newProperty;

public class GuiKeys implements IPropertyHolder {

    @Override
    public void registerComments(@NonNull final CommentsBuilder configuration) {
        final List<String> deprecation = List.of(
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens. Please read the latest changelogs",
                ""
        );

        configuration.setComment("Settings related to guis.", "gui");
        configuration.setComment("Inventory settings like size.", "gui", "inventory");
        configuration.setComment("The buttons in the gui.", "gui", "inventory", "buttons");

        configuration.setComment("The main menu button.", "gui", "inventory", "buttons", "menu");
        configuration.setComment("The next button.", "gui", "inventory", "buttons", "next");
        configuration.setComment("gui.inventory.buttons.back", "The back button.", "gui", "inventory", "buttons", "back");

        configuration.setComment("gui.inventory.filler", "Allows you to fill the gui with a singular item.", "gui", "inventory", "buttons", "filler");

        configuration.setComment("Allows you to configure items per slot.", "gui", "inventory", "buttons", "customizer");
    }

    @Override
    public void registerAliases(@NonNull final AliasBuilder builder) {
        builder.addAlias("buttons", "gui", "inventory", "buttons");
    }

    @Comment("If /crates should open the main menu. Warning: This will remove the menu button from crate previews.")
    public static final Property<Boolean> is_crate_menu_enabled = newProperty(true, "gui", "toggle");

    @Comment("The name of the gui.")
    public static final Property<String> crate_menu_inventory_name = newProperty("<bold><gradient:#e91e63:blue>Crazy Crates</gradient></bold>", "gui", "inventory", "name");

    @Comment("The amount of rows for a gui.")
    public static final Property<Integer> crate_menu_inventory_rows = newProperty(5, "gui", "inventory", "rows");

    @Comment("The name of the item.")
    @Alias("buttons")
    public static final Property<String> gui_filler_name = newProperty(" ", "gui", "inventory", "buttons", "filler", "name");

    @Comment("The lore of the item.")
    @Alias("buttons")
    public static final Property<List<String>> gui_filler_lore = newProperty(List.of(), "gui", "inventory", "buttons", "filler", "lore");

    @Comment("The item the button should be.")
    @Alias("buttons")
    public static final Property<String> menu_button_item = newProperty("compass", "gui", "inventory", "buttons", "menu", "item");

    @Comment({
            "This will disable our current functionality of our main menu button in crate previews.",
            "It allows you to override and use a menu of your choice from your plugin using a command."
    })
    @Alias("buttons")
    public static final Property<Boolean> menu_button_override = newProperty(false, "gui", "inventory", "buttons", "menu", "override", "toggle");

    @Comment("Should the customizer should be enabled?")
    @Alias("buttons")
    public static final Property<Boolean> is_gui_customizer_enabled = newProperty(true, "gui", "inventory", "buttons", "customizer", "toggle");

    @Comment("The items to set to the gui.")
    @Alias("buttons")
    public static final Property<List<String>> gui_customizer = newProperty(List.of(
            "slot:0, item:red_stained_glass_pane, name: ",
            "slot:1, item:red_stained_glass_pane, name: ",
            "slot:2, item:red_stained_glass_pane, name: ",
            "slot:3, item:red_stained_glass_pane, name: ",
            "slot:4, item:red_stained_glass_pane, name: ",
            "slot:5, item:red_stained_glass_pane, name: ",
            "slot:6, item:red_stained_glass_pane, name: ",
            "slot:7, item:red_stained_glass_pane, name: ",
            "slot:8, item:red_stained_glass_pane, name: ",
            "slot:36, item:red_stained_glass_pane, name: ",
            "slot:37, item:red_stained_glass_pane, name: ",
            "slot:38, item:red_stained_glass_pane, name: ",
            "slot:39, item:red_stained_glass_pane, name: ",
            "slot:40, item:red_stained_glass_pane, name: ",
            "slot:41, item:red_stained_glass_pane, name: ",
            "slot:42, item:red_stained_glass_pane, name: ",
            "slot:43, item:red_stained_glass_pane, name: ",
            "slot:44, item:red_stained_glass_pane, name: ",
            "slot:9, item:blue_stained_glass_pane, name: ",
            "slot:18, item:blue_stained_glass_pane, name: ",
            "slot:27, item:blue_stained_glass_pane, name: ",
            "slot:17, item:blue_stained_glass_pane, name: ",
            "slot:26, item:blue_stained_glass_pane, name: ",
            "slot:35, item:blue_stained_glass_pane, name: ",
            "slot:10, item:cyan_stained_glass_pane, name: ",
            "slot:12, item:cyan_stained_glass_pane, name: ",
            "slot:14, item:cyan_stained_glass_pane, name: ",
            "slot:24, item:cyan_stained_glass_pane, name: ",
            "slot:16, item:cyan_stained_glass_pane, name: ",
            "slot:19, item:cyan_stained_glass_pane, name: ",
            "slot:20, item:cyan_stained_glass_pane, name: ",
            "slot:21, item:cyan_stained_glass_pane, name: ",
            "slot:22, item:cyan_stained_glass_pane, name: ",
            "slot:23, item:cyan_stained_glass_pane, name: ",
            "slot:24, item:cyan_stained_glass_pane, name: ",
            "slot:25, item:cyan_stained_glass_pane, name: ",
            "slot:28, item:cyan_stained_glass_pane, name: ",
            "slot:30, item:cyan_stained_glass_pane, name: ",
            "slot:31, item:cyan_stained_glass_pane, name: ",
            "slot:32, item:cyan_stained_glass_pane, name: ",
            "slot:33, item:cyan_stained_glass_pane, name: ",
            "slot:34, item:cyan_stained_glass_pane, name: "
    ), "gui", "inventory", "buttons", "customizer", "items");
}