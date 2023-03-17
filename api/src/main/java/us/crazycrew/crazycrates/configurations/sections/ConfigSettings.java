package us.crazycrew.crazycrates.configurations.sections;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Description: The config.yml options.
 */
public class ConfigSettings implements SettingsHolder {

    // Empty constructor required by SettingsHolder
    public ConfigSettings() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/crazycrew",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/discussions",
                "",
                "Legacy color codes such as &7,&c no longer work. You must use MiniMessage",
                "https://docs.advntr.dev/minimessage/format.html#color"
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };

        conf.setComment("crate-settings", header);
        conf.setComment("gui-settings.filler-items", deprecation);
        conf.setComment("gui-settings.customizer", deprecation);
    }

    @Comment({
            "Warning: The log file as is may cause your server to crash.",
            "It is recommended to clear it occasionally.",
            "",
            "Option will always be set to false by default."
    })
    public static final Property<Boolean> LOG_TO_FILE = newProperty("crate-settings.crate-actions.log-to-file", false);

    @Comment("Whether you want to log crate actions to console or not.")
    public static final Property<Boolean> LOG_TO_CONSOLE = newProperty("crate-settings.crate-actions.log-to-console", false);

    @Comment("Whether you want /crates to open a menu or not.")
    public static final Property<Boolean> PREVIEW_MENU_TOGGLE = newProperty("crate-settings.preview-menu.toggle", true);

    @Comment("The title of /crates menu.")
    public static final Property<String> PREVIEW_MENU_NAME = newProperty("crate-settings.preview-menu.name", "<purple>Crazy Crates</purple>");

    @Comment("The size of /crates menu. | Must be a multiply of 9 so 9,18,27,36,45")
    public static final Property<Integer> PREVIEW_MENU_SIZE = newProperty("crate-settings.preview-menu.size", 45);

    @Comment({
            "Whether you want crates to knock you back if you have no keys.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static final Property<Boolean> CRATE_KNOCK_BACK = newProperty("crate-settings.knock-back", true);

    @Comment("Whether to notify the player they were given a virtual key when inventory is not empty.")
    public static final Property<Boolean> GIVE_VIRTUAL_KEYS_MESSAGE = newProperty("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", true);

    @Comment("Whether to give virtual keys to a player if inventory is not empty.")
    public static final Property<Boolean> GIVE_VIRTUAL_KEYS = newProperty("crate-settings.keys.inventory-not-empty.give-virtual-keys", true);

    @Comment({
            "Whether the sound should play or not.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static final Property<Boolean> KEY_SOUND_TOGGLE = newProperty("crate-settings.keys.key-sound.toggle", true);

    @Comment("The sound that plays when a player tries to open a crate without keys.")
    public static final Property<String> KEY_SOUND_NAME = newProperty("crate-settings.keys.key-sound.name", "ENTITY_VILLAGER_NO");

    @Comment("Whether physical crates accept virtual keys or not.")
    public static final Property<Boolean> PHYSICAL_ACCEPTS_VIRTUAL = newProperty("crate-settings.physical-accepts-virtual-keys", true);
    @Comment("Whether physical crates accept physical keys or not.")
    public static final Property<Boolean> PHYSICAL_ACCEPTS_PHYSICAL = newProperty("crate-settings.keys.physical-accepts-physical-keys", true);
    @Comment("Whether virtual crates accept physical keys or not.")
    public static final Property<Boolean> VIRTUAL_ACCEPTS_PHYSICAL_KEYS = newProperty("crate-settings.keys.virtual-accepts-physical-keys", true);

    @Comment({
            "How long a quad crate should be open?",
            "",
            "Warning: This option will be moved to be an option per crate.",
            "Moving this will allow each quad-crate to have different timers.",
            })
    public static final Property<Integer> QUAD_CRATE_TIMER = newProperty("crate-settings.quad-crate.timer", 300);

    @Comment({
            "Whether you want to deny crate usage in X world or not.",
            "",
            "Warning: This will potentially be moved to be an option per crate for more specific control."
            })
    public static final Property<Boolean> DISABLED_WORLDS_TOGGLE = newProperty("crate-settings.disabled-worlds.toggle", false);

    @Comment("The list of worlds to deny crate usage in.")
    public static final Property<List<String>> DISABLED_WORLDS = newListProperty("crate-settings.disabled-worlds.worlds", "");

    @Comment("The material of the menu button.")
    public static final Property<String> MENU_ITEM = newProperty("gui-settings.buttons.menu.compass", "COMPASS");

    @Comment("The name of the menu button.")
    public static final Property<String> MENU_NAME = newProperty("gui-settings.buttons.menu.item", "<purple>»</purple> <red>Menu</red> <purple>«</purple>");

    @Comment("The lore of the menu button.")
    public static final Property<List<String>> MENU_LORE = newListProperty("gui-settings.buttons.menu.lore", "<gray>Return to the menu.</gray>");

    @Comment("The material of the next button.")
    public static final Property<String> NEXT_ITEM = newProperty("gui-settings.buttons.next.item", "FEATHER");

    @Comment("The name of the next button.")
    public static final Property<String> NEXT_NAME = newProperty("gui-settings.buttons.next.name", "<gold>Next</gold> <gray>»</gray>");

    @Comment("The lore of the next button.")
    public static final Property<List<String>> NEXT_LORE = newListProperty("gui-settings.buttons.next.lore", "<gray>Page:</gray> <blue>%page%</blue>");

    @Comment("The material of the back button.")
    public static final Property<String> BACK_ITEM = newProperty("gui-settings.buttons.back.item", "FEATHER");

    @Comment("The name of the back button.")
    public static final Property<String> BACK_NAME = newProperty("gui-settings.buttons.back.name", "<gold>Back</gold> <gray>»</gray>");

    @Comment("The lore of the back button.")
    public static final Property<List<String>> BACK_LORE = newListProperty("gui-settings.buttons.back.lore", "<gray>Page:</gray> <blue>%page%</blue>");

    @Comment("Whether to the menu should be filled with items or not.")
    public static final Property<Boolean> FILLER_TOGGLE = newProperty("gui-settings.filler-items.toggle", false);

    @Comment("The item type you want to use.")
    public static final Property<String> FILLER_ITEM = newProperty("gui-settings.filler-items.item", "BLACK_STAINED_GLASS_PANE");

    @Comment("The name of the item.")
    public static final Property<String> FILLER_NAME = newProperty("gui-settings.filler-items.name", " ");

    @Comment("The lore of the item.")
    public static final Property<List<String>> FILLER_LORE = newListProperty("gui-settings.filler-items.lore", "");

    @Comment("Whether you want the customizer to be enabled.")
    public static final Property<Boolean> CUSTOMIZER_TOGGLE = newProperty("gui-settings.customizer.toggle", true);

    @Comment("Place any fancy item in the gui including custom items.")
    public static final Property<List<String>> CUSTOMIZER = newListProperty("gui-settings.customizer.items", List.of(
            "Slot:1, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:2, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:3, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:4, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:5, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:6, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:7, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:8, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:9, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:10, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:18, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:19, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:27, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:28, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:36, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:37, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:45, Item:BLACK_STAINED_GLASS_PANE, Name: ",

            "Slot:11, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:12, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:13, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:14, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:15, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:16, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:17, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:20, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:22, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:24, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:30, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:33, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:34, Item:WHITE_STAINED_GLASS_PANE, Name: ",
            "Slot:35, Item:WHITE_STAINED_GLASS_PANE, Name: ",

            "Slot:38, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:39, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:40, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:41, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:42, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:43, Item:BLACK_STAINED_GLASS_PANE, Name: ",
            "Slot:44, Item:BLACK_STAINED_GLASS_PANE, Name: "));
}