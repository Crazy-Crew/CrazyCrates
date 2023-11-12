package us.crazycrew.crazycrates.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Messages;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.common.utils.StringUtils;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.other.MsgUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Translation {

    no_teleporting(Messages.no_teleporting),
    no_commands_while_in_crate(Messages.no_commands_while_using_crate),
    feature_disabled(Messages.feature_disabled),
    no_keys(Messages.no_keys),
    no_virtual_key(Messages.no_virtual_key),
    not_on_block(Messages.not_on_block),
    already_opening_crate(Messages.already_opening_crate),
    quick_crate_in_use(Messages.quick_crate_in_use),
    world_disabled(Messages.world_disabled),
    reloaded_plugin(Messages.reloaded_plugin),
    reloaded_forced_out_of_preview(Messages.reloaded_forced_out_of_preview),
    not_online(Messages.not_online),
    no_permission(Messages.no_permission),
    no_crate_permission(Messages.no_crate_permission),
    cant_be_a_virtual_crate(Messages.cant_be_a_virtual_crate),
    inventory_not_empty(Messages.inventory_not_empty),
    too_close_to_another_player(Messages.too_close_to_another_player),
    needs_more_room(Messages.needs_more_room),
    out_of_time(Messages.out_of_time),
    must_be_a_player(Messages.must_be_a_player),
    must_be_console_sender(Messages.must_be_console_sender),
    must_be_looking_at_block(Messages.must_be_looking_at_block),
    not_a_crate(Messages.not_a_crate),
    not_a_number(Messages.not_a_number),
    gave_a_player_keys(Messages.gave_a_player_keys),
    cannot_give_player_keys(Messages.cannot_give_player_keys),
    obtaining_keys(Messages.obtaining_keys),
    given_everyone_keys(Messages.given_everyone_keys),
    given_offline_player_keys(Messages.given_offline_player_keys),
    take_player_keys(Messages.take_players_keys),
    cannot_take_keys(Messages.cannot_take_keys),
    take_offline_player_keys(Messages.take_offline_player_keys),
    opened_a_crate(Messages.opened_a_crate),
    internal_error(Messages.internal_error),
    unknown_command(Messages.unknown_command),
    correct_usage(Messages.correct_usage),
    no_item_in_hand(Messages.no_item_in_hand),
    added_item_with_editor(Messages.added_item_with_editor),
    failed_to_add_item(Messages.failed_to_add_item),
    preview_disabled(Messages.preview_disabled),
    no_schematics_found(Messages.no_schematics_found),
    no_prizes_found(Messages.no_prizes_found),
    same_player(Messages.same_player),
    prize_error(Messages.prize_error),
    required_keys(Messages.required_keys),
    transfer_not_enough_keys(Messages.transfer_not_enough_keys),
    transfer_sent_keys(Messages.transfer_sent_keys),
    transfer_received_keys(Messages.transfer_received_keys),
    created_physical_crate(Messages.created_physical_crate, true),
    removed_physical_crate(Messages.removed_physical_crate),
    no_virtual_keys(Messages.no_virtual_keys),
    no_virtual_keys_header(Messages.no_virtual_keys_header, true),
    other_player_no_keys(Messages.other_player_no_keys),
    other_player_no_keys_header(Messages.other_player_header, true),
    per_crate(Messages.per_crate),
    help(Messages.help, true),
    admin_help(Messages.admin_help, true);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private String message;

    private boolean isList = false;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Translation(Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Translation(Property<List<String>> listProperty, boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final ConfigManager configManager = this.plugin.getCrazyHandler().getConfigManager();
    private final SettingsManager configuration = this.configManager.getMessages();

    private boolean isList() {
        return this.isList;
    }

    private @NotNull List<String> getPropertyList(Property<List<String>> properties) {
        return this.configuration.getProperty(properties);
    }

    private @NotNull String getProperty(Property<String> property) {
        return this.configuration.getProperty(property);
    }

    public String getString() {
        return getMessage().toString();
    }

    public Translation getMessage() {
        return getMessage(new HashMap<>());
    }

    public Translation getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders);
    }

    public Translation getMessage(Map<String, String> placeholders) {
        // Get the string first.
        String message;

        if (isList()) {
            message = StringUtils.convertList(getPropertyList(this.listProperty));
        } else {
            message = getProperty(this.property);
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        this.message = message;

        return this;
    }

    public String toString() {
        return MsgUtils.color(this.message.replaceAll("%prefix%", this.configManager.getPluginConfig().getProperty(PluginConfig.command_prefix)));
    }
}