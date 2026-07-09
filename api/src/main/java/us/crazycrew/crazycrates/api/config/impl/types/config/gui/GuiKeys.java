package us.crazycrew.crazycrates.api.config.impl.types.config.gui;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.impl.types.config.gui.beans.ButtonConfig;
import us.crazycrew.crazycrates.api.config.properties.builders.AliasBuilder;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import java.util.List;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newBeanProperty;
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
        configuration.setComment("The back button.", "gui", "inventory", "buttons", "back");

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

    @Comment("Allows you to fill the gui with a singular item.")
    public static final Property<ButtonConfig> gui_filler_button = newBeanProperty(ButtonConfig.class, new ButtonConfig().initialize(" ", List.of(), "black_stained_glass_pane", -1, -1), "gui", "inventory", "buttons", "filler");

    @Comment("The item the button should be.")
    public static final Property<String> menu_button_item = newProperty("compass", "gui", "inventory", "buttons", "menu", "item");

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> menu_button_model_data = newProperty("-1", "gui", "inventory", "buttons", "menu", "custom-model-data");

    @Comment("The name of the item.")
    public static final Property<String> menu_button_name = newProperty("<bold><gray>» <red>Menu <gray>«</bold>", "gui", "inventory", "buttons", "menu", "name");

    @Comment("The lore of the item.")
    public static final Property<List<String>> menu_button_lore = newProperty(List.of("<gray>Return to the menu."), "gui", "inventory", "buttons", "menu", "lore");

    @Comment("The namespace i.e. nexo")
    public static final Property<String> menu_button_namespace = newProperty("", "gui", "inventory", "buttons", "menu", "model", "namespace");

    @Comment("The id i.e. emerald_helmet")
    public static final Property<String> menu_button_id = newProperty("", "gui", "inventory", "buttons", "menu", "model", "id");

    @Comment("The slot for the item. -1 uses a fallback value.")
    public static final Property<Integer> menu_button_column = newProperty(5, "gui", "inventory", "buttons", "menu", "placement", "column");

    @Comment("The row for the item. You cannot use a number greater than the available rows of the inventory, -1 uses a fallback value.")
    public static final Property<Integer> menu_button_row = newProperty(5, "gui", "inventory", "buttons", "menu", "placement", "row");

    @Comment({
            "This will disable our current functionality of our main menu button in crate previews.",
            "It allows you to override and use a menu of your choice from your plugin using a command."
    })
    public static final Property<Boolean> menu_button_override = newProperty(false, "gui", "inventory", "buttons", "menu", "override", "toggle");

    @Comment("Commands to run when the main menu button is clicked. The override option above has to be set to true.")
    public static final Property<List<String>> menu_button_command_list = newProperty(List.of("see {player}"), "gui", "inventory", "buttons", "menu", "override", "list");

    @Comment("The item the button should be.")
    public static final Property<String> next_button_item = newProperty("feather", "gui", "inventory", "buttons", "next", "item");

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> next_button_model_data = newProperty("-1", "gui", "inventory", "buttons", "next", "custom-model-data");

    @Comment("The name of the item.")
    public static final Property<String> next_button_name = newProperty("<bold><gold>Next »</bold>", "gui", "inventory", "buttons", "next", "name");

    @Comment("The lore of the item.")
    public static final Property<List<String>> next_button_lore = newProperty(List.of("<bold><gray>Page:</bold> <blue>{page}"), "gui", "inventory", "buttons", "next", "lore");

    @Comment("The namespace i.e. nexo")
    public static final Property<String> next_button_namespace = newProperty("", "gui", "inventory", "buttons", "next", "model", "namespace");

    @Comment("The id i.e. emerald_helmet")
    public static final Property<String> next_button_id = newProperty("", "gui", "inventory", "buttons", "next", "model", "id");

    @Comment("The slot for the item. -1 uses a fallback value.")
    public static final Property<Integer> next_button_column = newProperty(6, "gui", "inventory", "buttons", "next", "placement", "column");

    @Comment("The row for the item. You cannot use a number greater than the available rows of the inventory, -1 uses a fallback value.")
    public static final Property<Integer> next_button_row = newProperty(5, "gui", "inventory", "buttons", "next", "placement", "row");

    @Comment("The item the button should be.")
    public static final Property<String> back_button_item = newProperty("feather", "gui", "inventory", "buttons", "back", "item");

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> back_button_model_data = newProperty("-1", "gui", "inventory", "buttons", "back", "custom-model-data");

    @Comment("The name of the item.")
    public static final Property<String> back_button_name = newProperty("<bold><gold>« Back</bold>", "gui", "inventory", "buttons", "back", "name");

    @Comment("The lore of the item.")
    public static final Property<List<String>> back_button_lore = newProperty(List.of("<bold><gray>Page:</bold> <blue>{page}"), "gui", "inventory", "buttons", "back", "lore");

    @Comment("The namespace i.e. nexo")
    public static final Property<String> back_button_namespace = newProperty("", "gui", "inventory", "buttons", "back", "model", "namespace");

    @Comment("The id i.e. emerald_helmet")
    public static final Property<String> back_button_id = newProperty("", "gui", "inventory", "buttons", "back", "model", "id");

    @Comment("The slot for the item. -1 uses a fallback value.")
    public static final Property<Integer> back_button_column = newProperty(4, "gui", "inventory", "buttons", "back", "placement", "column");

    @Comment("The row for the item. You cannot use a number greater than the available rows of the inventory, -1 uses a fallback value.")
    public static final Property<Integer> back_button_row = newProperty(5, "gui", "inventory", "buttons", "back", "placement", "row");

    @Comment("Should the customizer should be enabled?")
    public static final Property<Boolean> is_gui_customizer_enabled = newProperty(true, "gui", "inventory", "buttons", "customizer", "toggle");

    @Comment("The items to set to the gui.")
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