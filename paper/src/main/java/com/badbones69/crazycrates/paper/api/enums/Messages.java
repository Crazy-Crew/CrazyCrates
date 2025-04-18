package com.badbones69.crazycrates.paper.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazycrates.core.enums.State;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.core.utils.AdvUtils;
import org.bukkit.command.CommandSender;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.messages.CommandKeys;
import com.badbones69.crazycrates.core.config.impl.messages.CrateKeys;
import com.badbones69.crazycrates.core.config.impl.messages.ErrorKeys;
import com.badbones69.crazycrates.core.config.impl.messages.MiscKeys;
import com.badbones69.crazycrates.core.config.impl.messages.PlayerKeys;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    no_teleporting(MiscKeys.no_teleporting),
    no_commands_while_in_crate(MiscKeys.no_commands_while_using_crate),
    feature_disabled(MiscKeys.feature_disabled),
    lacking_flag(MiscKeys.lacking_flag),
    unknown_command(MiscKeys.unknown_command),
    correct_usage(MiscKeys.correct_usage),
    no_keys(MiscKeys.no_keys),
    no_virtual_key(MiscKeys.no_virtual_key),
    internal_error(ErrorKeys.internal_error),
    key_refund(ErrorKeys.key_refund),
    no_schematics_found(ErrorKeys.no_schematics_found),
    no_prizes_found(ErrorKeys.no_prizes_found),
    prize_not_found(ErrorKeys.prize_not_found),
    prize_error(ErrorKeys.prize_error),
    cannot_be_empty(ErrorKeys.cannot_be_empty),
    cannot_be_air(ErrorKeys.cannot_be_air),
    not_online(PlayerKeys.not_online),

    no_permission(PlayerKeys.no_permission),
    inventory_not_empty(PlayerKeys.inventory_not_empty),
    too_close_to_another_player(PlayerKeys.too_close_to_another_player),
    must_be_a_player(PlayerKeys.must_be_a_player),
    must_be_console_sender(PlayerKeys.must_be_console_sender),
    same_player(PlayerKeys.same_player),
    must_be_looking_at_block(PlayerKeys.must_be_looking_at_block),
    obtaining_keys(PlayerKeys.obtaining_keys),

    cannot_set_type(CrateKeys.cannot_set_type),
    not_on_block(CrateKeys.not_on_block),
    already_opening_crate(CrateKeys.already_opening_crate),
    crate_in_use(CrateKeys.crate_in_use),
    already_redeemed_prize(CrateKeys.already_redeemed_prize),
    world_disabled(CrateKeys.world_disabled),
    no_crate_permission(CrateKeys.no_crate_permission),
    cant_be_a_virtual_crate(CrateKeys.cant_be_a_virtual_crate),
    needs_more_room(CrateKeys.needs_more_room),
    out_of_time(CrateKeys.out_of_time),
    not_a_crate(CrateKeys.not_a_crate),
    not_a_key(CrateKeys.not_a_key),
    not_a_number(CrateKeys.not_a_number),
    preview_disabled(CrateKeys.preview_disabled),
    not_enough_keys(CrateKeys.not_enough_keys),
    editor_enter(CrateKeys.editor_enter),
    editor_already_in(CrateKeys.editor_already_in),
    editor_exit(CrateKeys.editor_exit),
    force_editor_exit(CrateKeys.force_editor_exit),
    created_physical_crate(CrateKeys.created_physical_crate, true),
    physical_crate_already_exists(CrateKeys.physical_crate_already_exists),
    physical_crate_overridden(CrateKeys.physical_crate_overridden),
    removed_physical_crate(CrateKeys.removed_physical_crate),
    crate_locations(CrateKeys.crate_locations, true),
    crate_locations_format(CrateKeys.crate_location_format),
    reloaded_forced_out_of_preview(CrateKeys.reloaded_forced_out_of_preview),
    crate_teleported(CrateKeys.crate_teleported),
    crate_cannot_teleport(CrateKeys.crate_cannot_teleport),
    crate_prize_max_pulls(CrateKeys.crate_prize_max_pulls),
    crate_prize_max_respins(CrateKeys.crate_prize_max_respins),
    crate_prize_max_respins_left(CrateKeys.crate_prize_max_respins_left),
    crate_prize_max_respins_none(CrateKeys.crate_prize_max_respins_none),
    crate_prize_respins_claimed(CrateKeys.crate_prize_respins_claimed),

    crate_prize_respins_redeemed(CrateKeys.crate_prize_respins_redeemed),
    crate_prize_respins_empty(CrateKeys.crate_prize_respins_empty),
    crate_prize_respin_not_claimed(CrateKeys.crate_prize_respin_not_claimed),

    gave_a_player_keys(CommandKeys.gave_a_player_keys),
    cannot_give_player_keys(CommandKeys.cannot_give_player_keys),
    given_everyone_keys(CommandKeys.given_everyone_keys),
    given_offline_player_keys(CommandKeys.given_offline_player_keys),
    take_player_keys(CommandKeys.take_players_keys),
    reloaded_plugin(CommandKeys.reloaded_plugin),
    cannot_take_keys(CommandKeys.cannot_take_keys),
    take_offline_player_keys(CommandKeys.take_offline_player_keys),
    opened_a_crate(CommandKeys.opened_a_crate),
    no_item_in_hand(CommandKeys.no_item_in_hand),
    added_item_with_editor(CommandKeys.added_item_with_editor),
    transfer_not_enough_keys(CommandKeys.transfer_not_enough_keys),
    error_migrating(CommandKeys.error_migrating),
    migration_not_available(CommandKeys.migration_not_available),
    migration_plugin_not_enabled(CommandKeys.migration_plugin_not_enabled),
    migration_no_crates_available(CommandKeys.migration_no_crates_available),
    successfully_migrated(CommandKeys.successfully_migrated, true),
    successfully_migrated_users(CommandKeys.successfully_migrated_users, true),
    transfer_sent_keys(CommandKeys.transfer_sent_keys),
    transfer_received_keys(CommandKeys.transfer_received_keys),
    no_virtual_keys(CommandKeys.no_virtual_keys),
    virtual_keys_header(CommandKeys.virtual_keys_header, true),
    other_player_no_keys(CommandKeys.other_player_no_keys),
    other_player_no_keys_header(CommandKeys.other_player_header, true),
    per_crate(CommandKeys.per_crate),

    help(CommandKeys.help, true),
    admin_help(CommandKeys.admin_help, true);

    private Property<String> property;

    private Property<List<String>> properties;
    private boolean isList = false;

    Messages(@NotNull final Property<String> property) {
        this.property = property;
    }

    Messages(@NotNull final Property<List<String>> properties, final boolean isList) {
        this.properties = properties;
        this.isList = isList;
    }

    private final SettingsManager config = ConfigManager.getConfig();

    private final SettingsManager messages = ConfigManager.getMessages();

    private boolean isList() {
        return this.isList;
    }

    public String getString() {
        return this.messages.getProperty(this.property);
    }

    public List<String> getList() {
        return this.messages.getProperty(this.properties);
    }

    public String getMessage(@NotNull final CommandSender sender) {
        return getMessage(sender, new HashMap<>());
    }

    public String getMessage(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        Map<String, String> placeholders = new HashMap<>() {{
            put(placeholder, replacement);
        }};

        return getMessage(sender, placeholders);
    }

    public String getMessage(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        return parse(sender, placeholders).replaceAll("\\{prefix}", this.config.getProperty(ConfigKeys.command_prefix));
    }

    public void sendMessage(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> sendRichMessage(sender, placeholder, replacement);
            case send_actionbar -> sendActionBar(sender, placeholder, replacement);
        }
    }

    public void sendMessage(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> sendRichMessage(sender, placeholders);
            case send_actionbar -> sendActionBar(sender, placeholders);
        }
    }

    public void sendMessage(@NotNull final CommandSender sender) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> sendRichMessage(sender);
            case send_actionbar -> sendActionBar(sender);
        }
    }

    public void sendActionBar(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final String msg = getMessage(sender, placeholder, replacement);

        if (msg.isBlank()) return;

        if (sender instanceof Player player) {
            player.sendActionBar(AdvUtils.parse(msg));
        }
    }

    public void sendActionBar(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        final String msg = getMessage(sender, placeholders);

        if (msg.isBlank()) return;

        if (sender instanceof Player player) {
            player.sendActionBar(AdvUtils.parse(msg));
        }
    }

    public void sendActionBar(@NotNull final CommandSender sender) {
        final String msg = getMessage(sender);

        if (msg.isBlank()) return;

        if (sender instanceof Player player) {
            player.sendActionBar(AdvUtils.parse(msg));
        }
    }

    public void sendRichMessage(@NotNull final CommandSender sender, @NotNull final String placeholder, @NotNull final String replacement) {
        final String msg = getMessage(sender, placeholder, replacement);

        if (msg.isBlank()) return;

        sender.sendRichMessage(msg);
    }

    public void sendRichMessage(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        final String msg = getMessage(sender, placeholders);

        if (msg.isBlank()) return;

        sender.sendRichMessage(msg);
    }

    public void sendRichMessage(@NotNull final CommandSender sender) {
        final String msg = getMessage(sender);

        if (msg.isBlank()) return;

        sender.sendRichMessage(msg);
    }

    public void migrate() {
        if (this.isList) {
            this.messages.setProperty(this.properties, AdvUtils.convert(this.messages.getProperty(this.properties), true));

            return;
        }

        this.messages.setProperty(this.property, AdvUtils.convert(this.messages.getProperty(this.property), true));
    }

    private @NotNull String parse(@NotNull final CommandSender sender, @NotNull final Map<String, String> placeholders) {
        String message;

        if (isList()) {
            message = StringUtils.toString(getList());
        } else {
            message = getString();
        }

        return MiscUtils.populatePlaceholders(sender, message, placeholders);
    }
}