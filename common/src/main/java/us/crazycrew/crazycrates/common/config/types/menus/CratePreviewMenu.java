package us.crazycrew.crazycrates.common.config.types.menus;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

import java.util.List;

public class CratePreviewMenu implements SettingsHolder {

    @Comment("The material the main menu button will be.")
    public static final Property<String> crate_preview_menu_button_material = PropertyInitializer.newProperty("settings.preview-buttons.menu-button.material", "COMPASS");

    @Comment("The name of the main menu button.")
    public static final Property<String> crate_preview_menu_button_name = PropertyInitializer.newProperty("settings.preview-buttons.menu-button.name", "&7&l»» &c&lMenu &7&l««");

    @Comment("The lore for the main menu button.")
    public static final Property<List<String>> crate_preview_menu_button_lore = PropertyInitializer.newListProperty("settings.preview-buttons.menu-button.lore", List.of(
            "&dReturn to the menu."
    ));

    @Comment("The material the next button will be.")
    public static final Property<String> crate_preview_next_button_material = PropertyInitializer.newProperty("settings.preview-buttons.next-button.material", "FEATHER");

    @Comment("The name of the next button.")
    public static final Property<String> crate_preview_next_button_name = PropertyInitializer.newProperty("settings.preview-buttons.next-button.name", "&6&lNext »");

    @Comment("The lore for the next button.")
    public static final Property<List<String>> crate_preview_next_button_lore = PropertyInitializer.newListProperty("settings.preview-buttons.next-button.lore", List.of(
            "&d&lPage:&b &b{page}"
    ));

    @Comment("The material the back button will be.")
    public static final Property<String> crate_preview_back_button_material = PropertyInitializer.newProperty("settings.preview-buttons.back-button.material", "FEATHER");

    @Comment("The name of the Back button.")
    public static final Property<String> crate_preview_back_button_name = PropertyInitializer.newProperty("settings.preview-buttons.back-button.name", "&6&l« Back");

    @Comment("The lore for the back button.")
    public static final Property<List<String>> crate_preview_back_button_lore = PropertyInitializer.newListProperty("settings.preview-buttons.back-button.lore", List.of(
            "&5&lPage: &b{page}"
    ));
}