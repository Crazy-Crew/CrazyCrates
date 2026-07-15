package us.crazycrew.crazycrates.api.config.impl.types.config.crate;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.annotations.Disabled;
import us.crazycrew.crazycrates.api.config.properties.builders.AliasBuilder;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import java.util.List;
import static us.crazycrew.crazycrates.api.config.properties.PropertyBuilder.newProperty;

public class CrateKeys implements IPropertyHolder {

    @Override
    public void registerComments(@NonNull CommentsBuilder configuration) {
        final List<String> deprecation = List.of(
                "",
                "Warning: This section is subject to change so it is considered deprecated.",
                "This is your warning before the change happens. Please read the latest changelogs",
                ""
        );

        configuration.setComment("Settings related to crates.", "crate");
        configuration.setComment("The preview settings.", "crate", "preview");
        configuration.setComment("Settings related to how keys function.", "crate", "keys");

        configuration.setComment("Settings related to a player's inventory is not empty.", "crate", "keys", "inventory-settings");

        configuration.setComment("Settings related to QuadCrate", "crate", "quad-crate");

        configuration.setComment("This section contains settings related to different crate types.", "crate", "types");
        configuration.setComment("This section is related to settings with the csgo crate type.", "crate", "types", "csgo");
    }

    @Override
    public void registerAliases(@NonNull final AliasBuilder builder) {
        builder.addAlias("csgo", "crate", "types", "csgo");
        builder.addAlias("keys", "crate", "keys");
    }

    @Comment({
            "This will switch how previews/opening the physical crates are handled.",
            "",
            " true -> Right click to open, Left click to preview",
            " false -> Left click to open, Right click to preview"
    })
    public static final Property<Boolean> crate_physical_interaction =
            newProperty(false, "crate", "interaction", "physical");

    @Comment({
            "This will switch how previews/opening the virtual crates are handled.",
            "",
            " true -> Right click to open, Left click to preview",
            " false -> Left click to open, Right click to preview"
    })
    public static final Property<Boolean> crate_virtual_interaction =
            newProperty(false, "crate", "interaction", "virtual");

    @Comment("Whether to show the display item when opening QuickCrate")
    public static final Property<Boolean> show_quickcrate_item =
            newProperty(true, "crate", "quickcrate-display-item");

    @Comment("If crates should knock you back if you have no keys.")
    @Disabled("This option has been moved to be per crate file.")
    public static final Property<Boolean> is_knock_back_enabled =
            newProperty(true, "crate", "knock-back");

    @Comment("If players should be forced to exit out of the preview during /crates reload")
    public static final Property<Boolean> take_out_of_preview =
            newProperty(true, "crate", "preview", "force-exit");

    @Comment("Send a message if they were forced out of the preview.")
    public static final Property<Boolean> send_preview_taken_out_message =
            newProperty(true, "crate", "preview", "send-message");

    @Comment({
            "If a player gets to the menu related to the Prizes gui, Should they be timed out?",
            "",
            "It will wait 10 seconds and if they already collected 3 prizes, It will only give one prize."
    })
    public static final Property<Boolean> cosmic_crate_timeout =
            newProperty(true, "crate", "cosmic-crate-timeout");

    @Comment("Should a physical crate accept virtual keys?")
    public static final Property<Boolean> physical_accepts_virtual_keys =
            newProperty(true, "crate", "keys", "physical-crate-accepts-virtual-keys");

    @Comment("Should a physical crate accept physical keys?")
    public static final Property<Boolean> physical_accepts_physical_keys =
            newProperty(true, "crate", "keys", "physical-crate-accepts-physical-keys");

    @Comment("Should a virtual crate accept physical keys?")
    public static final Property<Boolean> virtual_accepts_physical_keys =
            newProperty(true, "crate", "keys", "virtual-crate-accepts-physical-keys");

    @Comment("Should the player should be given virtual keys if inventory is not empty? If you leave it as false, All keys will be dropped on the ground.")
    public static final Property<Boolean> give_virtual_keys_when_inventory_full =
            newProperty(true, "crate", "keys", "inventory-settings", "give-virtual-keys");

    @Comment("Should the player should be notified when their inventory is not empty?")
    public static final Property<Boolean> notify_player_when_inventory_full =
            newProperty(true, "crate", "keys", "inventory-settings", "send-message");

    @Comment("Should a sound should be played if they have no key?")
    public static final Property<Boolean> need_key_sound_toggle =
            newProperty(true, "crate", "keys", "key-sound", "toggle");

    @Comment("https://mudkipdev.github.io/minecraft-sound-explorer/")
    public static final Property<String> need_key_sound =
            newProperty("entity.villager.no", "crate", "keys", "key-sound", "name");

    @Comment("How long should the quad crate be active?")
    public static final Property<Integer> quad_crate_timer =
            newProperty(300, "crate", "quad-crate", "timer");

    @Comment("What worlds do you want Crates to be disabled in?")
    @Disabled("This option has been moved to be per crate file.")
    public static final Property<List<String>> disabled_worlds = newProperty(List.of(), "crate", "disabled-worlds");

    @Comment("This option if set to true will force the crate to take the required keys set in the crate file")
    public static final Property<Boolean> use_required_keys = newProperty(false, "crate", "use-required-keys");

    @Comment({
            "Sets a static material in the csgo crate in slot 4 and 22",
            "This is above and below the prize.",
            "",
            "Set this to blank to have it populate with glass."
    })
    public static final Property<String> csgo_cycle_material = newProperty("gold_ingot", "crate", "types", "csgo", "cycling-material");

    @Comment({
            "Sets a static material in the csgo crate in slot 4 and 22",
            "This is above and below the prize and only shows up when the crate ends.",
            "",
            "Set this to blank to have it populate with glass."
    })
    public static final Property<String> csgo_finished_material = newProperty("iron_ingot", "crate", "types", "csgo", "finished-material");

    @Comment("Logs all crate actions to console if enabled.")
    public static final Property<Boolean> log_to_console = newProperty(false, "crate", "log-to-console");

    @Comment("Logs all crate/key actions to individual files, on plugin reload and shutdown. the files will zip to avoid size errors.")
    public static final Property<Boolean> log_to_file = newProperty(false, "crate", "log-to-file");

}