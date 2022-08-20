package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.utils.ConfigurationUtils;
import com.badbones69.crazycrates.utils.keys.Key;
import com.google.inject.Singleton;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.ConsoleCommandSender;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class LocaleFile extends ConfigurationUtils {

    @Key("prefix.logger")
    public String PREFIX_LOGGER = "<gray>[<gold>CrazyCrates</gold>]</gray>";

    @Key("prefix.command")
    public String PREFIX_COMMAND = "<white>[<gradient:#FE5F55:#392F5A>CrazyCrew</gradient>]</white>";

    // Misc
    @Key("misc.unknown-command")
    public String UNKNOWN_COMMAND = "<red>This command is not known.";

    @Key("misc.no-teleporting")
    public String NO_TELEPORTING = "<red>You may not teleport away while opening <gold>%crate%.";

    @Key("misc.no-commands")
    public String NO_COMMANDS = "<red>You are not allowed to use commands while opening <gold>%crate%.";

    @Key("misc.no-keys")
    public String NO_KEYS = "<red>You need a %key% <red>in your hand to use <gold>%crate%.";

    @Key("misc.no-virtual-keys")
    public String NO_VIRTUAL_KEYS = "<red>You need %key% <red>to open <gold>%crate%.";

    // Errors
    @Key("errors.no-prizes-found")
    public String NO_PRIZES_FOUND = "<red>This crate contains no prizes that you can win.";

    @Key("errors.no-schematics-found")
    public String NO_SCHEMATICS_FOUND = "<red>No schematic were found, Please re-generate them by deleting the folder or checking for errors!";

    @Key("errors.prize-error")
    public List<String> PRIZE_ERROR = new ArrayList<>() {{
        add("<red>An error has occurred while trying to give you the prize <gold>%prize%.");
        add("<yellow>This has occurred in %crate%. <yellow>Please notify your owner.");
    }};

    @Key("errors.internal-error")
    public String INTERNAL_ERROR = "<red>An internal error has occurred. Please check the console for the full error.";

    // Player handling
    @Key("player.requirements.too-many-args")
    public String TOO_MANY_ARGS = "<red>You put more arguments then I can handle.";

    @Key("player.requirements.not-enough-args")
    public String NOT_ENOUGH_ARGs = "<red>You did not supply enough arguments.";

    @Key("player.requirements.must-be-player")
    public String MUST_BE_PLAYER = "<red>You must be a player to use this command.";

    @Key("player.requirements.must-be-console-sender")
    public String MUST_BE_CONSOLE_SENDER = "<red>You must be using console to use this command.";

    @Key("player.requirements.must-be-looking-at-block")
    public String MUST_BE_LOOKING_AT_BLOCK = "<red>You must be looking at a block.";

    @Key("player.target-not-online")
    public String TARGET_NOT_ONLINE = "<red>The player <gold>%player% <red>is not online.";

    @Key("player.target-same-player")
    public String TARGET_SAME_PLAYER = "<red>You cannot use this command on yourself.";

    @Key("player.no-permission")
    public String NO_PERMISSION = "<red>You do not have permission to use that command!";

    @Key("player.inventory-not-empty")
    public String INVENTORY_NOT_EMPTY = "<red>Inventory is not empty, Please make room before opening <gold>%crate%.";

    @Key("player.obtaining-keys")
    public String OBTAINING_KEYS = "<gray>You have been given <gold>%amount% %key% <gray>Keys.";

    @Key("player.too-close-to-another-player")
    public String TOO_CLOSE_TO_ANOTHER_PLAYER = "<red>You are too close to a player that is opening their Crate.";

    // Crates
    @Key("crates.requirements.not-a-crate")
    public String NOT_A_CRATE = "<red>There is no crate called <gold>%crate%.";

    @Key("crates.requirements.not-a-number")
    public String NOT_A_NUMBER = "<gold>%number% <red>is not a number.";

    @Key("crates.not-a-block")
    public String NOT_A_BLOCK = "<red>You must be standing on a block to use <gold>%crate%.";

    @Key("crates.out-of-time")
    public String OUT_OF_TIME = "<red>You took <green>5 Minutes <red>to open the <gold>%crate% <red>so it closed.";

    @Key("crates.crate-preview-disabled")
    public String CRATE_PREVIEW_DISABLED = "<red>The preview for <gold>%crate% <red>is currently disabled.";

    @Key("crates.crate-already-open")
    public String CRATES_ALREADY_OPEN = "<red>You are already opening <gold>%crate%.";

    @Key("crates.crate-in-use")
    public String CRATES_IN_USE = "<gold>%crate% <red>is already in use. Please wait until it finishes!";

    @Key("crates.cannot-be-a-virtual-crate")
    public String CANNOT_BE_A_VIRTUAL_CRATE = "<gold>%crate% <red>cannot be used as a Virtual Crate. You have it set to <gold>%cratetype%";

    @Key("crates.need-more-room")
    public String NEED_MORE_ROOM = "<red>There is not enough space to open that here.";

    @Key("crates.world-disabled")
    public String WORLD_DISABLED = "<red>Crates are disabled in <green>%world%.";

    @Key("crates.physical-crate.created")
    public List<String> PHYSICAL_CRATE_CREATED = new ArrayList<>() {{
        add("<gray>You have set that block to <gold>%crate%.");
        add("<gray>To remove <gold>%crate%, <gray>Shift Click Break in Creative to remove.");
    }};

    @Key("crates.physical-crate.removed")
    public String PHYSICAL_CRATE_REMOVED = "<gray>You have removed <gold>%id%.";

    // Commands
    @Key("command.open.opened-a-crate")
    public String OPENED_A_CRAtE = "<gray>You have opened the <gold>%crate% <gray>crate for <gold>%player%.";

    @Key("command.give.given-player-keys")
    public String GIVEN_PLAYER_KEYS = "<gray>You have given <gold>%player% %amount% <gray>Keys.";

    @Key("command.give.given-everyone-keys")
    public String GIVEN_EVERYONE_KEYS = "<gray>You have given everyone <gold>%amount% <gray>Keys.";

    @Key("command.given-offline-player-keys")
    public String GIVEN_OFFLINE_PLAYER_KEYS = "<gray>You have given <gold>%amount% <gray>key(s) to the offline player <gold>%player%.";

    @Key("command.take.take-player-keys")
    public String TAKE_PLAYER_KEYS = "<gray>You have taken <gold>%amount% <gray>key(s) from <gold>%player%.";

    @Key("command.take.take-offline-player-keys")
    public String TAKE_OFFLINE_PLAYER_KEYS = "<gray>You have taken <gold>%amount% <gray>key(s) from the offline player <gold>%player%.";

    @Key("command.additem.no-item-in-hand")
    public String NO_ITEM_IN_HAND = "<red>You need to have an item in your hand to add it to <gold>%crate%.";

    @Key("command.additem.add-item-from-hand")
    public String ADD_ITEM_FROM_HAND = "<gray>The item has been added to <gold>%crate% as <gold>Prize #%prize%.";

    @Key("command.convert.no-files-to-convert")
    public String NO_FILES_TO_CONVERT = "<red>No available plugins to convert files.";

    @Key("command.convert.error-converting-files")
    public String ERROR_CONVERTING_FILES = "<red>An error has occurred while trying to convert files. We could not convert <green>%file% <red>so please check the console.";

    @Key("command.convert.successfully-converted-files")
    public String SUCCESSFULLY_CONVERTED_FILES = "<green>Plugin Conversion has succeeded!";

    @Key("command.reload.confirm-reload")
    public String CONFIRM_RELOAD = "<yellow>Are you sure you want to reload the plugin?";

    @Key("command.reload.reload-completed")
    public String RELOAD_COMPLETED = "<green>Plugin reload has been completed.";

    @Key("command.transfer.not-enough-keys")
    public String TRANSFER_NOT_ENOUGH_KEYS = "<red>You do not have enough keys to transfer.";

    @Key("command.transfer.transferred-keys")
    public String TRANSFERRED_KEYS = "<gray>You have transferred <green>%amount% %crate% <gray>keys to <red>%player%.";

    @Key("command.transfer.transferred-keys-received")
    public String TRANSFERRED_KEYS_RECEIVED = "<gray>You have received <green>%amount% <gold>%crate% <gray>keys from <red>%player%.";

    @Key("command.keys.personal.no-virtual-keys")
    public String KEYS_PERSONAL_NO_VIRTUAL_KEYS = "<dark_gray>(<red>!<dark_gray>) <gray>You currently do not have any virtual keys.";

    @Key("command.keys.personal.virtual-keys-header")
    public List<String> KEYS_PERSONAL_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("<dark_gray>(<red>!<dark_gray>) <gray>A list of your current amount of keys.");
    }};

    @Key("command.keys.other-player.no-virtual-keys")
    public String KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS = "<dark_gray>(<red>!<dark_gray>) <gray>The player <red>%player% <gray>does not have any keys.";

    @Key("command.keys.other-player.virtual-keys-header")
    public List<String> KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("<dark_gray>(<red>!<dark_gray>) <gray>A list of <red>%player%''s <gray>current amount of keys.");
    }};

    @Key("command.keys.crate-format")
    public String CRATE_FORMAT = "%crate% <dark_gray>»» <gold>%keys% keys.";

    @Key("command.player-help")
    public List<String> PLAYER_HELP = new ArrayList<>() {{
        add(" <dark_green>Crazy Crates Player Help!");
        add(" ");
        add(" <dark_gray>» <gold>/key [player] <gray>» <yellow>Check how many keys a player has.");
        add(" <dark_gray>» <gold>/cc <gray>» <yellow>Opens the crate menu.");
    }};

    @Key("command.admin-help")
    public List<String> ADMIN_HELP = new ArrayList<>() {{
        add(" <red>Crazy Crates Admin Help");
        add(" ");
        add(" <dark_gray>» <gold>/cc additem <crate> <prize> <gray>- <yellow>Add items in-game to a prize in a crate.");
        add(" <dark_gray>» <gold>/cc preview <crate> [player] <gray>- <yellow>Opens the preview of a crate for a player.");
        add(" <dark_gray>» <gold>/cc list <gray>- <yellow>Lists all crates.");
        add(" <dark_gray>» <gold>/cc open <crate> [player] <gray>- <yellow>Tries to open a crate for a player if they have a key.");
        add(" <dark_gray>» <gold>/cc forceopen <crate> [player] <gray>- <yellow>Opens a crate for a player for free.");
        add(" <dark_gray>» <gold>/cc tp <location> <gray>- <yellow>Teleport to a Crate.");
        add(" <dark_gray>» <gold>/cc give <physical/virtual> <crate> [amount] [player] <gray>- <yellow>Allows you to take keys from a player.");
        add(" <dark_gray>» <gold>/cc set <crate> <gray>- <yellow>Set the block you are looking at as a crate.");
        add(" <dark_gray>» <gold>/cc set Menu <gray>- <yellow>Set the block you are looking at to open the <red>/cc menu.");
        add(" <dark_gray>» <gold>/cc reload <gray>- <yellow>Reloads the config/data files.");
        add(" <dark_gray>» <gold>/cc set1/set2 <gray>- <yellow>Sets position <red>#1 <yellow>or <red>#2 for when making a new schematic for QuadCrates.");
        add(" <dark_gray>» <gold>/cc save <file name> <gray>- <yellow>Create a new nbt file in the schematics folder.");
        add(" ");
        add(" <dark_gray>» <gold>/key [player] <gray>- <yellow>Check how many keys a player has.");
        add(" <dark_gray>» <gold>/cc <gray>- <yellow>Opens the crate menu.");
        add(" ");
        add("<gray>You can find a list of permissions @ <yellow>https://github.com/badbones69/Crazy-Crates/wiki/Commands-and-Permissions");
    }};

    public void reload(Path path, String fileName, CrazyCrates plugin) {
        System.out.println(fileName);
        System.out.println(path.resolve("/locale/" + fileName));

        this.reload(path.resolve("/locale/" + fileName), LocaleFile.class, plugin);
    }

    public void send(Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(recipient, true, msg, placeholders);
    }

    public void send(Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        if (msg == null) return;

        for (String part : msg.split("\n")) {
            send(recipient, prefix, parse(part, placeholders));
        }
    }

    public void send(Audience recipient, Component component) {
        send(recipient, true, component);
    }

    public void send(Audience recipient, boolean prefix, Component component) {
        if (recipient instanceof ConsoleCommandSender) {
            recipient.sendMessage(prefix ? parse(PREFIX_LOGGER).append(component) : component);
        } else {
            recipient.sendMessage(prefix ? parse(PREFIX_COMMAND).append(component) : component);
        }
    }

    public Component parse(String msg, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(msg, placeholders);
    }
}