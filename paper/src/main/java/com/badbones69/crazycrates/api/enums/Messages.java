package com.badbones69.crazycrates.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.types.ConfigKeys;
import com.badbones69.crazycrates.common.config.types.MessageKeys;
import com.badbones69.crazycrates.common.utils.StringUtils;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    no_teleporting(MessageKeys.no_teleporting),
    no_commands_while_in_crate(MessageKeys.no_commands_while_using_crate),
    feature_disabled(MessageKeys.feature_disabled),
    no_keys(MessageKeys.no_keys),
    no_virtual_key(MessageKeys.no_virtual_key),
    not_on_block(MessageKeys.not_on_block),
    already_opening_crate(MessageKeys.already_opening_crate),
    quick_crate_in_use(MessageKeys.quick_crate_in_use),
    world_disabled(MessageKeys.world_disabled),
    reloaded_plugin(MessageKeys.reloaded_plugin),
    reloaded_forced_out_of_preview(MessageKeys.reloaded_forced_out_of_preview),
    not_online(MessageKeys.not_online),
    no_permission(MessageKeys.no_permission),
    no_crate_permission(MessageKeys.no_crate_permission),
    cant_be_a_virtual_crate(MessageKeys.cant_be_a_virtual_crate),
    inventory_not_empty(MessageKeys.inventory_not_empty),
    too_close_to_another_player(MessageKeys.too_close_to_another_player),
    needs_more_room(MessageKeys.needs_more_room),
    out_of_time(MessageKeys.out_of_time),
    must_be_a_player(MessageKeys.must_be_a_player),
    must_be_console_sender(MessageKeys.must_be_console_sender),
    must_be_looking_at_block(MessageKeys.must_be_looking_at_block),
    not_a_crate(MessageKeys.not_a_crate),
    not_a_number(MessageKeys.not_a_number),
    gave_a_player_keys(MessageKeys.gave_a_player_keys),
    cannot_give_player_keys(MessageKeys.cannot_give_player_keys),
    obtaining_keys(MessageKeys.obtaining_keys),
    given_everyone_keys(MessageKeys.given_everyone_keys),
    given_offline_player_keys(MessageKeys.given_offline_player_keys),
    take_player_keys(MessageKeys.take_players_keys),
    cannot_take_keys(MessageKeys.cannot_take_keys),
    take_offline_player_keys(MessageKeys.take_offline_player_keys),
    opened_a_crate(MessageKeys.opened_a_crate),
    internal_error(MessageKeys.internal_error),
    unknown_command(MessageKeys.unknown_command),
    correct_usage(MessageKeys.correct_usage),
    no_item_in_hand(MessageKeys.no_item_in_hand),
    added_item_with_editor(MessageKeys.added_item_with_editor),
    failed_to_add_item(MessageKeys.failed_to_add_item),
    preview_disabled(MessageKeys.preview_disabled),
    no_schematics_found(MessageKeys.no_schematics_found),
    no_prizes_found(MessageKeys.no_prizes_found),
    same_player(MessageKeys.same_player),
    prize_error(MessageKeys.prize_error),
    required_keys(MessageKeys.required_keys),
    transfer_not_enough_keys(MessageKeys.transfer_not_enough_keys),
    transfer_sent_keys(MessageKeys.transfer_sent_keys),
    transfer_received_keys(MessageKeys.transfer_received_keys),
    created_physical_crate(MessageKeys.created_physical_crate, true),
    removed_physical_crate(MessageKeys.removed_physical_crate),
    no_virtual_keys(MessageKeys.no_virtual_keys),
    no_virtual_keys_header(MessageKeys.no_virtual_keys_header, true),
    other_player_no_keys(MessageKeys.other_player_no_keys),
    other_player_no_keys_header(MessageKeys.other_player_header, true),
    per_crate(MessageKeys.per_crate),
    help(MessageKeys.help, true),
    admin_help(MessageKeys.admin_help, true);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private String message;

    private boolean isList = false;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Messages(Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Messages(Property<List<String>> listProperty, boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();
    @NotNull
    private final ConfigManager configManager = this.plugin.getCrazyHandler().getConfigManager();
    @NotNull
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

    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, null);
    }

    public String getMessage() {
        return getMessage(new HashMap<>(), null);
    }

    public String getMessage(Player player) {
        return getMessage(new HashMap<>(), player);
    }

    public String getMessage(String placeholder, String replacement, Player player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders, player);
    }

    public String getMessage(String placeholder, String replacement) {
        return getMessage(placeholder, replacement, null);
    }

    public String getMessage(Map<String, String> placeholders, Player player) {
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

        return asString(player);
    }

    private String asString(Player player) {
        String prefix = this.configManager.getConfig().getProperty(ConfigKeys.command_prefix);

        String message = this.message.replaceAll("%prefix%", prefix);

        if (MiscUtils.isPapiActive() && player != null) {
            return PlaceholderAPI.setPlaceholders(player, MsgUtils.color(message));
        }

        return MsgUtils.color(message);
    }
}