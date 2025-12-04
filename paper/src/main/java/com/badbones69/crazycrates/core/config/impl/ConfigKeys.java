package com.badbones69.crazycrates.core.config.impl;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazycrates.core.config.beans.ModelData;
import com.badbones69.crazycrates.core.config.beans.inventories.ItemPlacement;
import com.badbones69.crazycrates.core.enums.State;
import java.util.List;
import static ch.jalu.configme.properties.PropertyInitializer.newBeanProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newListProperty;
import static ch.jalu.configme.properties.PropertyInitializer.newProperty;

public class ConfigKeys implements SettingsHolder {

    protected ConfigKeys() {}

    @Override
    public void registerComments(CommentsConfiguration conf) {
        String[] header = {
                "Support: https://discord.gg/badbones-s-live-chat-182615261403283459",
                "Github: https://github.com/Crazy-Crew",
                "",
                "Issues: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "Features: https://github.com/Crazy-Crew/CrazyCrates/issues",
                "",
                "List of all sounds: https://minecraft.wiki/w/Sounds.json#Java_Edition_values",
        };

        String[] deprecation = {
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens. Please read the latest changelogs",
                ""
        };

        conf.setComment("gui", "Settings related to guis.");
        conf.setComment("gui.inventory", "Inventory settings like size.");
        conf.setComment("gui.inventory.buttons", "The buttons in the gui.");

        conf.setComment("gui.inventory.buttons.menu", "The main menu button.");
        conf.setComment("gui.inventory.buttons.next", "The next button.");
        conf.setComment("gui.inventory.buttons.back", "The back button.");

        conf.setComment("gui.inventory.filler", "Allows you to fill the gui with a singular item.");

        conf.setComment("gui.inventory.customizer", "Allows you to configure items per slot.");

        conf.setComment("crate", "Settings related to crates.");
        conf.setComment("crate.preview", "The preview settings.");
        conf.setComment("crate.keys", "Settings related to how keys function.");

        conf.setComment("crate.keys.inventory-settings", "Settings related to a player's inventory is not empty.");

        conf.setComment("crate.quad-crate", "Settings related to QuadCrate");

        conf.setComment("crate.types", "This section contains settings related to different crate types.");
        conf.setComment("crate.types.csgo", "This section is related to settings with the csgo crate type.");

        conf.setComment("root", header);
    }

    @Comment({
            "This option will let you test a different way of picking random numbers. If you have any issues, You can set it back to false.",
            "",
            "If the option is set to false, items should be more random in theory."
    })
    public static final Property<Boolean> use_different_random = newProperty("root.use-different-random", false);

    @Comment({
            "This will allow you to use a new items layout for Prizes which should be much more straight forward, less prone to errors.",
            "You can run /crazycrates migrate -mt NewItemFormat which should migrate all existing prizes after you set this to true",
            "",
            "Take a backup before you run the migration as there may be bugs."
    })
    public static final Property<Boolean> use_different_items_layout = newProperty("root.use-different-items-layout", false);

    @Comment("Sends anonymous statistics to https://bstats.org/plugin/bukkit/CrazyCrates/4514")
    public static final Property<Boolean> toggle_metrics = newProperty("root.toggle-metrics", true);

    @Comment({
            "A recent change to permissions related to opening crates was made",
            "The way I assumed wildcard permissions worked isn't how they worked",
            "The superperms system for wildcards is stupid... but I digress",
            "",
            "It feels right to make a toggle for it regardless.",
            "",
            "false -> crazycrates.open.<crate-name>",
            "true -> crazycrates.deny.open.<crate_name>",
            "",
            "This option is being removed, crazycrates.open.<crate-name> will be the permission going forward.",
            "Changing this option requires you to restart your server!"
    })
    public static final Property<Boolean> use_new_permission_system = newProperty("root.use-new-permission-system", false);

    @Comment({
            "This option will tell the plugin to send all messages as action bars or messages in chat.",
            "",
            "send_message -> sends messages in chat.",
            "send_actionbar -> sends messages in actionbar.",
            ""
    })
    public static final Property<State> message_state = newBeanProperty(State.class, "root.message-state", State.send_message);

    @Comment({
            "A list of available hologram plugins:",
            " -> DecentHolograms",
            " -> FancyHolograms",
            " -> CMI",
            " -> None",
            "",
            "If the option is set to blank, it'll pick whatever plugin it feels like picking.",
            "Set the value to None if you do not want any."
    })
    public static final Property<String> hologram_plugin = newProperty("root.hologram-plugin", "");

    @Comment("This will wipe the example folder on /crazycrates reload or plugin startup. so you always have fresh examples to look at.")
    public static final Property<Boolean> update_examples_folder = newProperty("root.update-examples-folder", true);

    @Comment("The prefix used in commands")
    public static final Property<String> command_prefix = newProperty("root.command_prefix", " <gradient:#e91e63:blue>CrazyCrates</gradient> | ");

    @Comment("If /crates should open the main menu. Warning: This will remove the menu button from crate previews.")
    public static final Property<Boolean> enable_crate_menu = newProperty("gui.toggle", true);

    @Comment("The name of the gui.")
    public static final Property<String> inventory_name = newProperty("gui.inventory.name", "<bold><gradient:#e91e63:blue>Crazy Crates</gradient></bold>");

    @Comment("The amount of rows for a gui.")
    public static final Property<Integer> inventory_rows = newProperty("gui.inventory.rows", 5);

    @Comment({
            "This will switch how previews/opening the physical crates are handled.",
            "",
            " true -> Right click to open, Left click to preview",
            " false -> Left click to open, Right click to preview"
    })
    public static final Property<Boolean> crate_physical_interaction = newProperty("crate.interaction.physical", true);

    @Comment({
            "This will switch how previews/opening the virtual crates are handled.",
            "",
            " true -> Right click to open, Left click to preview",
            " false -> Left click to open, Right click to preview"
    })
    public static final Property<Boolean> crate_virtual_interaction = newProperty("crate.interaction.virtual", false);

    @Comment("Whether to show the display item when opening QuickCrate")
    public static final Property<Boolean> show_quickcrate_item = newProperty("crate.quickcrate-display-item", true);

    @Comment("If crates should knock you back if you have no keys.")
    public static final Property<Boolean> knock_back = newProperty("crate.knock-back", true);

    @Comment("If players should be forced to exit out of the preview during /crates reload")
    public static final Property<Boolean> take_out_of_preview = newProperty("crate.preview.force-exit", false);

    @Comment("Send a message if they were forced out of the preview.")
    public static final Property<Boolean> send_preview_taken_out_message = newProperty("crate.preview.send-message", false);

    @Comment({
            "If a player gets to the menu related to the Prizes gui, Should they be timed out?",
            "",
            "It will wait 10 seconds and if they already collected 3 prizes, It will only give one prize."
    })
    public static final Property<Boolean> cosmic_crate_timeout = newProperty("crate.cosmic-crate-timeout", true);

    @Comment("Should a physical crate accept virtual keys?")
    public static final Property<Boolean> physical_accepts_virtual_keys = newProperty("crate.keys.physical-crate-accepts-virtual-keys", true);

    @Comment("Should a physical crate accept physical keys?")
    public static final Property<Boolean> physical_accepts_physical_keys = newProperty("crate.keys.physical-crate-accepts-physical-keys", true);

    @Comment("Should a virtual crate accept physical keys?")
    public static final Property<Boolean> virtual_accepts_physical_keys = newProperty("crate.keys.virtual-crate-accepts-physical-keys", true);

    @Comment("Should the player should be given virtual keys if inventory is not empty? If you leave it as false, All keys will be dropped on the ground.")
    public static final Property<Boolean> give_virtual_keys_when_inventory_full = newProperty("crate.keys.inventory-settings.give-virtual-keys", false);

    @Comment("Should the player should be notified when their inventory is not empty?")
    public static final Property<Boolean> notify_player_when_inventory_full = newProperty("crate.keys.inventory-settings.send-message", false);

    @Comment("Should a sound should be played if they have no key?")
    public static final Property<Boolean> need_key_sound_toggle = newProperty("crate.keys.key-sound.toggle", true);

    @Comment({
            "The type of sound to use.",
            "https://minecraft.wiki/w/Sounds.json#Java_Edition_values"
    })
    public static final Property<String> need_key_sound = newProperty("crate.keys.key-sound.name", "entity.villager.no");

    @Comment("How long should the quad crate be active?")
    public static final Property<Integer> quad_crate_timer = newProperty("crate.quad-crate.timer", 300);

    @Comment("What worlds do you want Crates to be disabled in?")
    public static final Property<List<String>> disabled_worlds = newListProperty("crate.disabled-worlds", List.of(
            "world_nether"
    ));

    @Comment("This option if set to true will force the crate to take the required keys set in the crate file")
    public static final Property<Boolean> crate_use_required_keys = newProperty("crate.use-required-keys", false);

    @Comment({
            "Sets a static material in the csgo crate in slot 4 and 22",
            "This is above and below the prize.",
            "",
            "Set this to blank to have it populate with glass."
    })
    public static final Property<String> crate_csgo_cycling_material = newProperty("crate.types.csgo.cycling-material", "gold_ingot");

    @Comment({
            "Sets a static material in the csgo crate in slot 4 and 22",
            "This is above and below the prize and only shows up when the crate ends.",
            "",
            "Set this to blank to have it populate with glass."
    })
    public static final Property<String> crate_csgo_finished_material = newProperty("crate.types.csgo.finished-material", "iron_ingot");

    @Comment("Logs all crate actions to console if enabled.")
    public static final Property<Boolean> log_to_console = newProperty("crate.log-to-console", false);

    @Comment("Logs all crate/key actions to individual files, on plugin reload and shutdown. the files will zip to avoid size errors.")
    public static final Property<Boolean> log_to_file = newProperty("crate.log-to-file", false);

    @Comment("The item the button should be.")
    public static final Property<String> menu_button_item = newProperty("gui.inventory.buttons.menu.item", "compass");

    @Comment({
            "This will disable our current functionality of our main menu button in crate previews.",
            "It allows you to override and use a menu of your choice from your plugin using a command."
    })
    public static final Property<Boolean> menu_button_override = newProperty("gui.inventory.buttons.menu.override.toggle", false);

    @Comment({
            "A list of commands to run when the main menu button is clicked. The override option above has to be set to true.",
    })
    public static final Property<List<String>> menu_button_command_list = newListProperty("gui.inventory.buttons.menu.override.list", List.of("see {player}"));

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> menu_button_model_data = newProperty("gui.inventory.buttons.menu.custom-model-data", "");
    @Comment({
            "The item model, Mojang introduced this in 1.21.4... this replaces custom model data!",
            "Set this to blank for it to do nothing."
    })
    public static final Property<ModelData> menu_button_item_model = newBeanProperty(ModelData.class, "gui.inventory.buttons.menu.model", new ModelData().init());


    @Comment("The name of the item.")
    public static final Property<String> menu_button_name = newProperty("gui.inventory.buttons.menu.name", "<bold><gray>» <red>Menu <gray>«</bold>");

    @Comment("The lore of the item.")
    public static final Property<List<String>> menu_button_lore = newListProperty("gui.inventory.buttons.menu.lore", List.of(
            "<gray>Return to the menu."
    ));

    @Comment("The placement of the menu button. -1 defaults to an internal row.")
    public static final Property<ItemPlacement> menu_button_placement = newBeanProperty(ItemPlacement.class, "gui.inventory.buttons.menu.placement", new ItemPlacement().init(5));

    @Comment("The item the button should be.")
    public static final Property<String> next_button_item = newProperty("gui.inventory.buttons.next.item", "feather");

    @Comment("The placement of the next button. -1 defaults to an internal row.")
    public static final Property<ItemPlacement> next_button_placement = newBeanProperty(ItemPlacement.class, "gui.inventory.buttons.next.placement", new ItemPlacement().init(6));

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> next_button_model_data = newProperty("gui.inventory.buttons.next.custom-model-data", "-1");

    @Comment({
            "The item model, Mojang introduced this in 1.21.4... this replaces custom model data!",
            "Set this to blank for it to do nothing."
    })
    public static final Property<ModelData> next_button_item_model = newBeanProperty(ModelData.class, "gui.inventory.buttons.next.model", new ModelData().init());

    @Comment("The name of the item.")
    public static final Property<String> next_button_name = newProperty("gui.inventory.buttons.next.name", "<bold><gold>Next »</bold>");

    @Comment("The lore of the item.")
    public static final Property<List<String>> next_button_lore = newListProperty("gui.inventory.buttons.next.lore", List.of(
            "<bold><gray>Page:</bold> <blue>{page}"
    ));

    @Comment("The item the button should be.")
    public static final Property<String> back_button_item = newProperty("gui.inventory.buttons.back.item", "feather");

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> back_button_model_data = newProperty("gui.inventory.buttons.back.custom-model-data", "");

    @Comment("The placement of the back button. -1 defaults to an internal row.")
    public static final Property<ItemPlacement> back_button_placement = newBeanProperty(ItemPlacement.class, "gui.inventory.buttons.back.placement", new ItemPlacement().init(4));

    @Comment({
            "The item model, Mojang introduced this in 1.21.4... this replaces custom model data!",
            "Set this to blank for it to do nothing."
    })
    public static final Property<ModelData> back_button_item_model = newBeanProperty(ModelData.class, "gui.inventory.buttons.back.model", new ModelData().init());

    @Comment("The name of the item.")
    public static final Property<String> back_button_name = newProperty("gui.inventory.buttons.back.name", "<bold><gold>« Back</bold>");

    @Comment("The lore of the item.")
    public static final Property<List<String>> back_button_lore = newListProperty("gui.inventory.buttons.back.lore", List.of(
            "<bold><gray>Page:</bold> <blue>{page}"
    ));

    @Comment("Should the menu should be filled with one type of item?")
    public static final Property<Boolean> filler_toggle = newProperty("gui.inventory.buttons.filler.toggle", false);

    @Comment("The item to fill the menu with.")
    public static final Property<String> filler_item = newProperty("gui.inventory.buttons.filler.item", "black_stained_glass_pane");

    @Comment("The custom model data for the item, -1 or blank is disabled.")
    public static final Property<String> filler_model_data = newProperty("gui.inventory.buttons.filler.custom-model-data", "");

    @Comment({
            "The item model, Mojang introduced this in 1.21.4... this replaces custom model data!",
            "Set this to blank for it to do nothing."
    })
    public static final Property<ModelData> filler_item_model = newBeanProperty(ModelData.class, "gui.inventory.buttons.filler.model", new ModelData().init());

    @Comment("The name of the item.")
    public static final Property<String> filler_name = newProperty("gui.inventory.buttons.filler.name", " ");

    @Comment("The lore of the item.")
    public static final Property<List<String>> filler_lore = newListProperty("gui.inventory.buttons.filler.lore", List.of());

    @Comment("Should the customizer should be enabled?")
    public static final Property<Boolean> gui_customizer_toggle = newProperty("gui.inventory.buttons.customizer.toggle", true);

    @Comment("The items to set to the gui.")
    public static final Property<List<String>> gui_customizer = newListProperty("gui.inventory.buttons.customizer.items", List.of(
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
    ));
}