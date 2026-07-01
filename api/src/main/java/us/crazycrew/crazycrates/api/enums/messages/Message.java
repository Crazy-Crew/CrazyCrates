package us.crazycrew.crazycrates.api.enums.messages;

import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.YamlMessageAdapter;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.adapters.sender.ISenderAdapter;
import java.util.List;
import java.util.Map;
import static us.crazycrew.crazycrates.api.CrazyCrates.namespace;

@NullMarked
public enum Message {

    command_opened_crate("Messages.Opened-A-Crate", "command.open.opened-a-crate", "{prefix}<gray>You have opened the {crate} <gray>for <gold>{player}.", "command", "open", "opened-a-crate"),
    command_gave_player_keys("Messages.Given-A-Player-Keys", "command.give.given-player-keys", "{prefix}<gray>You have given <gold>{player} {amount} <gray>key(s).", "command", "give", "given-player-keys"),
    command_cannot_give_player_keys("Messages.Cannot-Give-Player-Keys", "command.give.full-inventory", "{prefix}<gray>You have been given <gold>{amount} {key} <gray>virtual key(s) because your inventory was full.", "command", "give", "full-inventory"),
    command_gave_everyone_keys("Messages.Given-Everyone-Keys", "command.give.given-everyone-keys", "{prefix}<gray>You have given everyone <gold>{amount} <gray>key(s).", "command", "give", "given-everyone-keys"),
    command_gave_offline_player_keys("Messages.Given-Offline-Player-Keys", "command.give.given-offline-player-keys", "{prefix}<gray>You have given <gold>{amount} <gray>key(s) to the offline player <gold>{player}.", "command", "give", "given-offline-player-keys"),
    command_take_player_keys("Messages.Take-A-Player-Keys", "command.take.take-player-keys", "{prefix}<gray>You have taken <gold>{amount} <gray>key(s) from <gold>{player}.", "command", "take", "take-player-keys"),
    command_cant_take_keys("Messages.Cannot-Take-Keys", "command.take.cannot-take-keys", "{prefix}<gray>You cannot take key(s) from <gold>{player} <gray>as they are poor.", "command", "take", "cannot-take-keys"),
    command_take_offline_player_keys("Messages.Take-Offline-Player-Keys", "command.take.take-offline-player-keys", "{prefix}<gray>You have taken <gold>{amount} <gray>key(s) from the offline player <gold>{player}.", "command", "take", "take-offline-player-keys"),

    command_editor_item_not_in_hand("Messages.No-Item-In-Hand", "command.additem.no-item-in-hand", "{prefix}<red>You need to have an item in your hand to add it {crate}.", "command", "additem", "no-item-in-hand"),
    command_editor_item_added("Messages.Added-Item-With-Editor", "command.additem.add-item-from-hand", "{prefix}<gray>The item has been added to the {crate} in prize #{prize}.", "command", "additem", "add-item-from-hand"),

    command_migrate_error("", "command.migrate.error", "{prefix}<red>We could not migrate <green>{file} <red>using <green>{type} <red>migration for <green>{reason}.", "command", "migrate", "error"),
    command_migrate_not_available("", "command.migrate.not.available", "{prefix}<green>This migration type is not available.", "command", "migrate", "not-available"),
    command_migrate_plugin_disabled("", "command.migrate.plugin.disabled", "{prefix}<green>The plugin <red>{name} <green>is not enabled. Cannot use as migration!", "command", "migrate", "plugin-not-available"),
    command_migrate_no_crates_present("", "command.migrate.no.crates.present", "{prefix}<green>There is no crates available for migration!", "command", "migrate", "no-crates-available"),
    command_migrate_users_success("", "command.migrate.users.succcess", StringUtils.toString(List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Migration Stats ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Successful Conversions: ",
            " ⤷ {succeeded_amount}</green>",
            "<dark_gray>»</dark_gray> <red>Failed Conversions: ",
            " ⤷ {failed_amount}</red>",
            "",
            "<red>Conversion Time: <yellow>{time}",
            "<red>Conversion Type: <yellow>{type}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    )), "command", "migrate", "success-users"),
    command_migrate_success("", "command.migrate.success", StringUtils.toString(List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Migration Stats ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Successful Conversions: ",
            " ⤷ {succeeded_amount}</green>",
            "<dark_gray>»</dark_gray> <red>Failed Conversions: ",
            " ⤷ {failed_amount}</red>",
            "",
            "<red>Conversion Time: <yellow>{time}",
            "<red>Conversion Type: <yellow>{type}",
            "",
            "<red>Converted Files:",
            "{files}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    )), "command", "migrate", "success"),

    command_key_refund("", "command.key.refund", "{prefix}<red>An error has occurred with the crate {crate} that you were opening, A refund for your key has been given.", "errors", "key-refund"),

    command_reload("Messages.Reload", "{prefix}<dark_aqua>You have reloaded the Config and Data Files.", "command", "reload", "completed"),

    command_transfer_not_enough_keys("Messages.Transfer-Keys.Not-Enough-Keys", "command.transfer.not.enough.keys", "{prefix}<red>You do not have enough keys to transfer.", "command", "transfer", "not-enough-keys"),
    command_transfer_sent_keys("Messages.Transfer-Keys.Transferred-Keys", "command.transfer.sent.keys", "{prefix}<gray>You have transferred {amount} {crate} keys to {player}.", "command", "transfer", "transferred-keys"),
    command_transfer_received_keys("Messages.Transfer-Keys.Received-Transferred-Keys", "command.transfer.received.keys", "{prefix}<gray>You have received {amount} {crate} keys from {player}.", "command", "transfer", "transferred-keys-received"),

    command_keys_no_virtual_keys("Messages.Keys.Personal.No-Virtual-Keys", "command.keys.no.virtual.keys", "{prefix}<bold><dark_gray>(<dark_red>!<dark_gray>)</bold> <gray>You currently do not have any virtual keys.", "command", "keys", "personal", "no-virtual-keys"),

    command_keys_virtual_keys_header("Messages.Keys.Personal.Header", "command.keys.no.virtual.keys.header", StringUtils.toString(List.of(
            "<bold><dark_gray>(<gold>!<dark_gray>)</bold> <gray>List of your current number of keys.",
            " <yellow> -> Total Crates Opened: <red>{crates_opened}",
            ""
    )), "command", "keys", "personal", "virtual-keys-header"),

    command_keys_target_player_no_keys("Messages.Keys.Other-Player.No-Virtual-Keys", "command.keys.target.player.no.keys", "{prefix}<bold><dark_gray>(<dark_red>!<dark_gray>)</bold> <gray>The player {player} does not have any keys.", "command", "keys", "other-player", "no-virtual-keys"),
    command_keys_target_player_header("Messages.Keys.Other-Player.Header", "command.keys.target.player.header", StringUtils.toString(List.of(
            "<bold><dark_gray>(<gold>!<dark_gray>)</bold> <gray>List of {player}''s current number of keys.",
            " <yellow> -> Total Crates Opened: <red>{crates_opened}",
            ""
    )), "command", "keys", "other-player", "virtual-keys-header"),

    command_keys_crate_format("Messages.Keys.Per-Crate", "command.keys.crate.format", "{crate} <bold><gray>><dark_gray>></bold> <gold>{keys} keys <gray>: Opened <gold>{crate_opened} times", "command", "keys", "crate-format"),

    command_admin_help("Messages.Admin-Help", "command.admin.help", StringUtils.toString(List.of(
            "<bold><red>Crazy Crates Admin Help</bold>",
            "",
            "<gold>/crates additem <crate_name> <prize_number> <chance> [tier] <gray>- <yellow>Add items in-game to a prize in a crate including Cosmic/Casino.",
            "<gold>/crates preview <crate_name> [player] <gray>- <yellow>Opens the preview of a crate for a player.",
            "<gold>/crates list <gray>- <yellow>Lists all crates.",
            "<gold>/crates open <crate_name> <gray>- <yellow>Tries to open a crate for you if you have a key.",
            "<gold>/crates open-others <crate_name> [player] <gray>- <yellow>Tries to open a crate for a player if they have a key.",
            "<gold>/crates transfer <crate_name> [player] [amount <gray>- <yellow>Transfers keys to players you chose.",
            "<gold>/crates debug <gray>- <yellow>Debugs crates",
            "<gold>/crates admin <gray>- <yellow>Shows admin menu",
            "<gold>/crates forceopen <crate_name> [player] <gray>- <yellow>Opens a crate for a player for free.",
            "<gold>/crates mass-open <crate_name> <physical/virtual> [amount] <gray>- <yellow>Mass opens a set amount of crates.",
            "<gold>/crates tp <location> <gray>- <yellow>Teleport to a Crate.",
            "<gold>/crates give <physical/virtual> <crate_name> [amount] [player] [-s/--silent] <gray>- <yellow>Allows you to take keys from a player.",
            "<gold>/crates give-random <physical/virtual> <amount> [-s/--silent] <gray>- <yellow>Gives a player random crate keys.",
            "<gold>/crates give-all <physical/virtual> <crate> <amount> [-s/--silent] <gray>- <yellow>Gives all online players keys to use on a crate.",
            "<gold>/crates editor -c/--crate <crate_name> or -e/--exit <gray>- <yellow>Allows you to enter/exit editor mode.",
            "<gold>/crates migrate -mt/--migration_type <migration_type> --crate/-crate <crate> --data/-d <gray>- <yellow>A command to migrate from other plugins or for internal changes.",
            "<gold>/crates reload <gray>- <yellow>Reloads the config/data files.",
            "<gold>/crates set1/set2 <gray>- <yellow>Sets position <red>#1 <yellow>or <red>#2 <yellow>for when making a new schematic for QuadCrates.",
            "<gold>/crates save <file name> <gray>- <yellow>Create a new nbt file in the schematics folder.",
            "",
            "<gold>/keys view [player] <gray>- <yellow>Check the number of keys a player has.",
            "<gold>/keys <gray>- <yellow>Shows how many keys you have.",
            "<gold>/crates <gray>- <yellow>Opens the menu.",
            "",
            "<gray>You can find a list of permissions @ <yellow>https://docs.crazycrew.us/mods/crazycrates/reference/commands/"
    )), "command", "admin-help"),
    command_help("Messages.Help", "command.help", StringUtils.toString(List.of(
            "<bold><yellow>Crazy Crates Player Help</bold>",
            "",
            "<gold>/keys view [player] <gray>- <yellow>Check the number of keys a player has.",
            "<gold>/keys <gray>- <yellow>Shows how many keys you have.",
            "<gold>/crates <gray>- <yellow>Opens the menu."
    )), "command",  "player-help"),

    prize_not_found("", "errors.prize.not.found", "{prefix}<red>This crate contains no prizes that you can win.", "errors", "no-prizes-found"),
    prize_error("Messages.Prize-Error", "errors.prize.error", "{prefix}<red>An error has occurred trying to give you a prize from crate <gold>{crate}<red>.", "errors", "prize-error"),

    schematics_empty("Messages.No-Schematics-Found", "errors.schematics.empty", "{prefix}<red>No schematics were found. Files must end in .nbt", "errors", "no-schematics-found"),
    prizes_empty("Messages.No-Prizes-Found", "errors.prizes.empty", "{prefix}<red>This crate contains no prizes that you can win.", "errors", "no-prizes-found"),

    cannot_be_empty("", "errors.cannot.be.empty", "{prefix}<red>{value} cannot be empty!", "errors", "cannot-be-empty"),
    cannot_be_air("", "errors.cannot.be.air", "{prefix}<red>You can't use air silly!~", "errors", "cannot-be-air"),

    internal_error("Messages.Internal-Error", "errors.internal.error", "{prefix}<red>An internal error has occurred. Please check the console for the full error.", "errors", "internal-error"),

    no_virtual_keys("Messages.No-Virtual-Key", "misc.no.virtual.keys", "{prefix}<red>You need a virtual key to open <gold>{crate}.", "misc", "no-virtual-keys"),
    no_keys("Messages.No-Key", "misc.no.keys", "{prefix}<red>You must have a {key} <red>in your hand to use <gold>{crate}.", "misc", "no-keys"),

    no_teleporting_in_crate("Messages.No-Teleporting", "misc.no.teleporting.in.crate", "{prefix}<red>You may not teleport away while opening a crate.", "misc", "no-teleporting"),
    no_command_in_crate("Messages.No-Commands-While-In-Crate", "misc.no.command.in.crate", "{prefix}<red>You are not allowed to use commands while opening crates.", "misc", "no-commands"),

    command_unknown("Messages.Unknown-Command", "misc.unknown.command", "{prefix}<red>{command} is not a known command.", "misc", "unknown-command"),
    command_usage("Messages.Correct-Usage", "misc.correct.command.usage", "{prefix}<red>The correct usage for this command is <yellow>{usage}.", "misc", "correct-usage"),
    command_lacking_flag("", "misc.lacking.flag", "prefix}<red>{flag} is not present in the command, expected format: {usage}", "misc", "lacking-flag"),

    must_be_player("Messages.Must-Be-A-Player", "player.must.be.player", "{prefix}<red>You must be a player to use this command.", "player", "requirements", "must-be-player"),
    must_be_console_sender("Messages.Must-Be-A-Console-Sender", "player.must.be.console.sender", "{prefix}<red>You must be using console to use this command.", "player", "requirements", "must-be-console-sender"),
    must_be_looking_at_block("Messages.Must-Be-Looking-At-A-Block", "player.must.be.looking.at.block", "{prefix}<red>You must be looking at a block.", "player", "requirements", "must-be-looking-at-block"),

    must_not_be_same_player("Messages.Same-Player", "player.must.not.be.same.player", "{prefix}<red>You cannot use this command on yourself.", "player", "target-same-player"),

    must_not_be_next_to_player("Messages.To-Close-To-Another-Player", "player.must.not.be.next.to.player", "{prefix}<red>You are too close to a player that is opening a crate.", "player", "too-close-to-another-player"),

    inventory_not_empty("Messages.Inventory-Full", "player.inventory.not.empty", "{prefix}<red>Inventory is not empty, Please make room before opening <gold>{crate}.", "player", "inventory-not-empty"),
    lacking_permission("Messages.No-Permission", "player.lacking.permission", "{prefix}<red>You do not have permission to use that command/menu!", "player", "no-permission"),

    obtaining_keys("Messages.Obtaining-Keys", "player.obtaining.keys", "{prefix}<gray>You have been given <gold>{amount} {key} <gray>key(s).", "player", "obtaining-keys"),

    not_online("Messages.Not-Online", "player.not.online", "{prefix}<red>{player} <gray>is not online.", "player", "target-not-online"),
    not_a_crate("Messages.Not-A-Crate", "crates.not.a.crate", "{prefix}<red>There is no crate called <gold>{crate}.", "crates", "requirements", "not-a-crate"),
    not_a_key("", "crates.not.a.key", "{prefix}<red>There is no key called <gold>{key}.", "crates", "requirements", "not-a-key"),
    not_a_number("Messages.Not-A-Number", "crates.not.a.number", "{prefix}<gold>{number} <red>is not a number.", "crates", "requirements", "not-a-number"),
    not_enough_keys("Messages.Required-Keys", "crates.not.enough.keys", "{prefix}<gray>You need <red>{key_amount} <gray>keys to open <red>{crate}. <gray>You have <red>{amount}.", "crates", "requirements", "not-enough-keys"),
    not_on_block("Messages.Not-On-Block", "crates.not.on.block", "{prefix}<red>You must be standing on a block to use <gold>{crate}.", "crates", "not-a-block"),
    not_physical_crate("Messages.Cant-Be-A-Virtual-Crate", "crates.not.physical.crate", "{prefix}<gold>{crate} <red>cannot be used as a Virtual Crate.", "crates", "cannot-be-a-virtual-crate"),
    not_enough_room("Messages.Needs-More-Room", "crates.not.enough.room", "{prefix}<red>There is not enough space to open that here", "crates", "needs-more-room"),

    out_of_time("Messages.Out-Of-Time", "crates.out.of.time", "{prefix}<red>You took <gold>5 minutes <red>to open <gold>{crate} <red>so it closed.", "crates", "out-of-time"),

    world_disabled("Messages.Cannot-Set-Menu-Type", "crates.world.disabled", "{prefix}<red>I am sorry, but crates are disabled in {world}.", "crates", "world-disabled"),

    preview_force_exit("Messages.Forced-Out-Of-Preview", "crates.preview.force.exit", "{prefix}<red>A reload has forced you out of the preview.", "crates", "forced-out-of-preview"),
    preview_disabled("Messages.Preview-Disabled", "crates.preview.disabled", "{prefix}<red>You cannot set the Menu to a block because the crate menu is disabled", "crates", "crate-preview-disabled"),

    crate_already_opened("Messages.Already-Opening-Crate", "crates.crate.already.open", "{prefix}<red>You are already opening <gold>{crate}.", "crates", "crate-already-open"),
    crate_already_used("Messages.Quick-Crate-In-Use", "crates.crate.already.used", "{prefix}<red>{crate} is already in use. Please wait until it finishes.", "crates", "crate-in-use"),

    crate_cannot_set_type("Messages.Cannot-Set-Menu-Type", "crates.cannot.set.menu.type", "{prefix}<red>You cannot set the Menu to a block because the crate menu is disabled", "crates", "cannot-set-menu-type"),

    crate_no_permission("Messages.No-Permission", "crates.no.permission", "{prefix}<red>You do not have permission to use {crate}.", "crates", "crate-no-permission"),

    crate_prize_redeemed("", "crates.prize.redeemed", "{prefix}<red>You have already redeemed this prize!", "crates", "already-redeemed-prize"),

    crate_editor_enter("", "crates.editor.enter", "{prefix}<red>You are now in editor mode.", "crates", "editor", "enter"),
    crate_editor_exit("", "crates.editor.exit", "{prefix}<red>You are no longer in editor mode.", "crates", "editor", "exit"),
    crate_editor_enabled("", "crates.editor.enabled", "{prefix}<red>You are already in the editor mode.", "crates", "editor", "already-in"),
    crate_editor_force_exit("", "crates.editor.force-exit", "{prefix}<red>You have been forced out of the editor mode for {reason}.", "crates", "editor", "force-exit"),

    physical_crate_created("Messages.Created-Physical-Crate", "crates.physical.crate.created", StringUtils.toString(List.of(
            "{prefix}<gray>You have set that block to {crate}.",
            "<gray>To remove the crate shift break in creative to remove."
    )), "crates", "physical-crate", "created"),
    physical_crate_overwrite("", "crates.physical.crate.overwrite", "{prefix}<gray>You have overridden the crate's location with id <gold>{id} with {new_crate}.", "crates", "physical-crate", "override"),
    physical_crate_exists("", "crates.physical.crate.exists", "{prefix}<gray>This location already has a crate named <gold>{crate} <gray>with id: <gold>{id}.", "crates", "physical-crate", "exists"),
    physical_crate_removed("", "crates.physical.crate.removed", "{prefix}<gray>You have removed <gold>{id}.", "crates", "physical-crate", "removed"),

    crate_respins_max_none("", "crates.respins.max.none", "N/A", "crates", "respins", "none"),
    crate_respins_max("", "crates.respins.max", "{prefix}<red>You can no longer respin, Status: {status}", "crates", "respins", "max"),

    crate_respins_claimed("", "crates.respins.claimed", "{prefix}<red>You have claimed {amount} prizes from your respins!", "crates", "respins", "claimed"),
    crate_respins_redeemed("", "crates.respins.redeemed", "{prefix}<red>You have redeemed your prize with the name {prize} from the crate named {crate}!", "crates", "respins", "redeemed"),
    crate_respins_not_claimed("", "crates.respins.not.claimed", "{prefix}<red>If you didn't get your prize, please run /crazycrates claim {crate}", "crates", "respins", "not-claimed"),
    crate_respins_empty("", "crates.respins.empty", "{prefix}There is no prize to claim from the crate {crate}.", "crates", "respins", "empty"),
    crate_respins_format("", "crates.respins.format", "{respins_left}/{respins_total}", "crates", "respins", "format"),

    crate_teleport_failed("", "crates.teleport.failed", "{prefix}<red>There is no location with the name: <gold>{id}.", "crates", "teleport", "failed"),
    crate_teleport_success("", "crates.teleport.success", "red>You have been teleported to the location with the name: <gold>{name}.", "crates", "teleport", "success"),

    crate_list_per_crate("Messages.Keys.Per-Crate", "crates.list.per-crate", "<dark_gray>[<blue>{id}<dark_gray>]: <red>{crate_name}<dark_gray>, <red>{world}<dark_gray>, <red>{x}<dark_gray>, <red>{y}<dark_gray>, <red>{z}", "crates", "list", "per-crate"),
    crate_list_format("", "crates.list.format", StringUtils.toString(List.of(
            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Crate Statistics ━━━━━━━━━━━━━━━━━━━</gold></bold>",
            "<dark_gray>»</dark_gray> <green>Active Crates: ",
            " ⤷ {active_crates}</green>",
            "<dark_gray>»</dark_gray> <red>Broken Crates: ",
            " ⤷ {broken_crates}</red>",
            "<dark_gray>»</dark_gray> <yellow>Crate Locations: ",
            " ⤷ {active_locations}</yellow>",
            "",
            "{locations}",
            "",
            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
    )), "crates", "list", "format"),

    crate_pulls_max("", "crates.pulls.max", "{prefix}<red>This prize can no longer be obtained, {pulls}/{maxpulls}", "crates", "pulls", "max"),

    feature_disabled("Messages.Feature-Disabled", "misc.feature.disabled", "{prefix}<red>This feature is disabled.", "misc", "feature-disabled");

    private final CrazyCrates platform = CratesProvider.api();

    private final ISenderAdapter senderAdapter = this.platform.getSenderAdapter();

    private final State state = this.platform.getMessageState();

    private final String defaultValue;
    private final FusionKey id;
    private final Object[] path;
    private String oldPath;

    Message(final String id, final String defaultValue, final Object... path) {
        this.id = FusionKey.key(namespace, id);
        this.defaultValue = defaultValue;
        this.path = path;

        this.oldPath = "";
    }

    Message(final String oldPath, final String id, final String defaultValue, final Object... path) {
        this(id, defaultValue, path);

        this.oldPath = oldPath;
    }

    public void addKey(final MessageRegistry registry, final CommentedConfigurationNode configuration, final FusionKey id) {
        registry.addKey(
                id,
                this.id,
                new YamlMessageAdapter(
                        configuration,
                        this.defaultValue,
                        this.path
                )
        );
    }

    public void sendMessage(final Audience audience, final Map<String, String> placeholders) {
        switch (this.state) {
            case send_message -> this.senderAdapter.sendMessage(audience, this.id, placeholders);
            case send_actionbar -> this.senderAdapter.sendActionBar(audience, this.id, placeholders);
        }
    }

    public void sendMessage(final Audience audience, final String placeholder, final String value) {
        sendMessage(audience, Map.of(placeholder, value));
    }

    public void sendMessage(final Audience audience) {
        sendMessage(audience, Map.of());
    }

    public String getMessage(final Audience audience, final Map<String, String> placeholders) {
        return this.senderAdapter.getMessage(audience, this.id, placeholders);
    }

    public String getMessage(final Audience audience, final String placeholder, final String value) {
        return getMessage(audience, Map.of(placeholder, value));
    }

    public String getMessage(final Audience audience) {
        return this.senderAdapter.getMessage(audience, this.id);
    }

    public FusionKey getKey() {
        return this.id;
    }
}