package us.crazycrew.crazycrates.common.config.types;

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
public class Config implements SettingsHolder {

    protected Config() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                ""
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens.",
                ""
        };

        conf.setComment("crate-settings", header);
        conf.setComment("crate-settings.keys.key-sound", deprecation);
        conf.setComment("gui-settings.filler-items", deprecation);
        conf.setComment("gui-settings.customizer", deprecation);
    }

    @Comment({
            "Warning: The log file as is may cause your server to crash.",
            "It is recommended to clear it occasionally.",
            "",
            "Option will always be set to false by default."
    })
    public static final Property<Boolean> log_to_file = newProperty("crate-settings.crate-actions.log-to-file", false);

    @Comment("Whether you want to log crate actions to console or not.")
    public static final Property<Boolean> log_to_console = newProperty("crate-settings.crate-actions.log-to-console", false);

    @Comment({
            "Whether you want crates to knock you back if you have no keys.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static final Property<Boolean> crate_knock_back = newProperty("crate-settings.knock-back", true);

    @Comment("Whether to notify the player they were given a virtual key when inventory is not empty.")
    public static final Property<Boolean> give_virtual_keys_message = newProperty("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", true);

    @Comment("Whether to give virtual keys to a player if inventory is not empty.")
    public static final Property<Boolean> give_virtual_keys = newProperty("crate-settings.keys.inventory-not-empty.give-virtual-keys", true);

    @Comment({
            "Whether sound should play or not.",
            "",
            "Warning: This option will be moved to be an option per crate."
    })
    public static final Property<Boolean> key_sound_toggle = newProperty("crate-settings.keys.key-sound.toggle", true);

    @Comment("The sound that plays when a player tries to open a crate without keys.")
    public static final Property<String> key_sound_name = newProperty("crate-settings.keys.key-sound.name", "ENTITY_VILLAGER_NO");

    @Comment("Whether physical crates accept virtual keys or not.")
    public static final Property<Boolean> physical_accepts_virtual = newProperty("crate-settings.keys.physical-accepts-virtual-keys", true);
    @Comment("Whether physical crates accept physical keys or not.")
    public static final Property<Boolean> physical_accepts_physical = newProperty("crate-settings.keys.physical-accepts-physical-keys", true);
    @Comment("Whether virtual crates accept physical keys or not.")
    public static final Property<Boolean> virtual_accepts_physical_keys = newProperty("crate-settings.keys.virtual-accepts-physical-keys", true);

    @Comment({
            "How long a quad crate should be open?",
            "",
            "Warning: This option will be moved to be an option per crate.",
            "Moving this will allow each quad-crate to have different timers.",
            })
    public static final Property<Integer> quad_crate_timer = newProperty("crate-settings.quad-crate.timer", 300);

    @Comment({
            "Whether you want to deny crate usage in X world or not.",
            "",
            "Warning: This will potentially be moved to be an option per crate for more specific control."
            })
    public static final Property<Boolean> disabled_worlds_toggle = newProperty("crate-settings.disabled-worlds.toggle", false);

    @Comment("The list of worlds to deny crate usage in.")
    public static final Property<List<String>> disabled_worlds = newListProperty("crate-settings.disabled-worlds.worlds", List.of(
            "world_nether"
    ));

    @Comment("Whether you want the customizer to be enabled.")
    public static final Property<Boolean> customizer_toggle = newProperty("gui-settings.customizer.toggle", true);

    @Comment("Place any fancy item in the gui including custom items.")
    public static final Property<List<String>> customizer_item_list = newListProperty("gui-settings.customizer.items", List.of(
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