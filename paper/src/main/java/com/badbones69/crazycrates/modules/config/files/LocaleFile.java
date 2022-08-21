package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.modules.config.AbstractConfig;
import com.badbones69.crazycrates.utils.keys.Comment;
import com.badbones69.crazycrates.utils.keys.Key;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.ConsoleCommandSender;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LocaleFile extends AbstractConfig {

    @Key("prefix.logger")
    @Comment("Change how the prefix in console will look!")
    public static String PREFIX_LOGGER = "<white>[<gradient:#FE5F55:#6b55b5>CrazyCrates</gradient>]</white>";

    @Key("prefix.command")
    @Comment("Change how the prefix for commands will look!")
    public static String PREFIX_COMMAND = "<white>[<gradient:#FE5F55:#6b55b5>CrazyCrates</gradient>]</white>";

    // Misc
    @Key("misc.unknown-command")
    public static String UNKNOWN_COMMAND = "<red>This command is not known.</red>";

    @Key("misc.no-teleporting")
    public static String NO_TELEPORTING = "<red>You may not teleport away while opening</red> <gold>%crate%.</gold>";

    @Key("misc.no-commands")
    public static String NO_COMMANDS = "<red>You are not allowed to use commands while opening</red> <gold>%crate%.</gold>";

    @Key("misc.no-keys")
    public static String NO_KEYS = "<red>You need a</red> %key% <red>in your hand to use</red> <gold>%crate%.</gold>";

    @Key("misc.no-virtual-keys")
    public static String NO_VIRTUAL_KEYS = "<red>You need</red> %key% <red>to open</red> <gold>%crate%.</gold>";

    // Errors
    @Key("errors.no-prizes-found")
    public static String NO_PRIZES_FOUND = "<red>This crate contains no prizes that you can win.</red>";

    @Key("errors.no-schematics-found")
    public static String NO_SCHEMATICS_FOUND = "<red>No schematic were found, Please re-generate them by deleting the folder or checking for errors!</red>";

    @Key("errors.prize-error")
    public static List<String> PRIZE_ERROR = new ArrayList<>() {{
        add("<red>An error has occurred while trying to give you the prize</red> <gold>%prize%.</gold>");
        add("<yellow>This has occurred in</yellow> <gold>%crate%</gold>. <yellow>Please notify your owner.</yellow>");
    }};

    @Key("errors.internal-error")
    public static String INTERNAL_ERROR = "<red>An internal error has occurred. Please check the console for the full error.</red>";

    // Player handling
    @Key("player.requirements.too-many-args")
    public static String TOO_MANY_ARGS = "<red>You put more arguments then I can handle.</red>";

    @Key("player.requirements.not-enough-args")
    public static String NOT_ENOUGH_ARGs = "<red>You did not supply enough arguments.</red>";

    @Key("player.requirements.must-be-player")
    public static String MUST_BE_PLAYER = "<red>You must be a player to use this command.</red>";

    @Key("player.requirements.must-be-console-sender")
    public static String MUST_BE_CONSOLE_SENDER = "<red>You must be using console to use this command.</red>";

    @Key("player.requirements.must-be-looking-at-block")
    public static String MUST_BE_LOOKING_AT_BLOCK = "<red>You must be looking at a block.</red>";

    @Key("player.target-not-online")
    public static String TARGET_NOT_ONLINE = "<red>The player</red> <gold>%player%</gold> <red>is not online.</red>";

    @Key("player.target-same-player")
    public static String TARGET_SAME_PLAYER = "<red>You cannot use this command on yourself.</red>";

    @Key("player.no-permission")
    public static String NO_PERMISSION = "<red>You do not have permission to use that command!</red>";

    @Key("player.inventory-not-empty")
    public static String INVENTORY_NOT_EMPTY = "<red>Inventory is not empty, Please make room before opening</red> <gold>%crate%.</gold>";

    @Key("player.obtaining-keys")
    public static String OBTAINING_KEYS = "<gray>You have been given</gray> <gold>%amount% %key%</gold> <gray>Keys.</gray>";

    @Key("player.too-close-to-another-player")
    public static String TOO_CLOSE_TO_ANOTHER_PLAYER = "<red>You are too close to a player that is opening their Crate.</red>";

    // Crates
    @Key("crates.requirements.not-a-crate")
    public static String NOT_A_CRATE = "<red>There is no crate called</red> <gold>%crate%.</gold>";

    @Key("crates.requirements.not-a-number")
    public static String NOT_A_NUMBER = "<gold>%number%</gold> <red>is not a number.</red>";

    @Key("crates.not-a-block")
    public static String NOT_A_BLOCK = "<red>You must be standing on a block to use</red> <gold>%crate%.</gold>";

    @Key("crates.out-of-time")
    public static String OUT_OF_TIME = "<red>You took</red> <green>5 Minutes</green> <red>to open the</red> <gold>%crate%</gold> <red>so it closed.</red>";

    @Key("crates.crate-preview-disabled")
    public static String CRATE_PREVIEW_DISABLED = "<red>The preview for</red> <gold>%crate%</gold> <red>is currently disabled.</red>";

    @Key("crates.crate-already-open")
    public static String CRATES_ALREADY_OPEN = "<red>You are already opening</red> <gold>%crate%</gold>.";

    @Key("crates.crate-in-use")
    public static String CRATES_IN_USE = "<gold>%crate%</gold> <red>is already in use. Please wait until it finishes!</red>";

    @Key("crates.cannot-be-a-virtual-crate")
    public static String CANNOT_BE_A_VIRTUAL_CRATE = "<gold>%crate%</gold> <red>cannot be used as a Virtual Crate. You have it set to</red> <gold>%cratetype%.</gold>";

    @Key("crates.need-more-room")
    public static String NEED_MORE_ROOM = "<red>There is not enough space to open that here.</red>";

    @Key("crates.world-disabled")
    public static String WORLD_DISABLED = "<red>Crates are disabled in</red> <green>%world%.</green>";

    @Key("crates.physical-crate.created")
    public static List<String> PHYSICAL_CRATE_CREATED = new ArrayList<>() {{
        add("<gray>You have set that block to</grey> <gold>%crate%.</gold>");
        add("<gray>To remove</grey> <gold>%crate%</gold>, <gray>Shift Click Break in Creative to remove.</grey>");
    }};

    @Key("crates.physical-crate.removed")
    public static String PHYSICAL_CRATE_REMOVED = "<gray>You have removed</gray> <gold>%id%.</gold>";

    // Commands
    @Key("command.open.opened-a-crate")
    public static String OPENED_A_CRAtE = "<gray>You have opened the</gray> <gold>%crate%</gold> <gray>crate for</gray> <gold>%player%.</gold>";

    @Key("command.give.given-player-keys")
    public static String GIVEN_PLAYER_KEYS = "<gray>You have given</gray> <gold>%player% %amount% Keys.</gold>";

    @Key("command.give.given-everyone-keys")
    public static String GIVEN_EVERYONE_KEYS = "<gray>You have given everyone</gray> <gold>%amount% Keys.</gold>";

    @Key("command.given-offline-player-keys")
    public static String GIVEN_OFFLINE_PLAYER_KEYS = "<gray>You have given</gray> <gold>%amount% key(s)</gold> <gray>to the offline player</gray> <gold>%player%.</gold>";

    @Key("command.take.take-player-keys")
    public static String TAKE_PLAYER_KEYS = "<gray>You have taken</gray> <gold>%amount% key(s)</gold> <gray>from</gray> <gold>%player%.</gold>";

    @Key("command.take.take-offline-player-keys")
    public static String TAKE_OFFLINE_PLAYER_KEYS = "<gray>You have taken</gray> <gold>%amount% key(s)</gold> <gray>from the offline player</gray> <gold>%player%.</gold>";

    @Key("command.additem.no-item-in-hand")
    public static String NO_ITEM_IN_HAND = "<red>You need to have an item in your hand to add it to</red> <gold>%crate%.</gold>";

    @Key("command.additem.add-item-from-hand")
    public static String ADD_ITEM_FROM_HAND = "<gray>The item has been added to</gray> <gold>%crate%</gold> <gray>as</gray> <gold>Prize #%prize%.</gold>";

    @Key("command.convert.no-files-to-convert")
    public static String NO_FILES_TO_CONVERT = "<red>No available plugins to convert files.</red>";

    @Key("command.convert.error-converting-files")
    public static String ERROR_CONVERTING_FILES = "<red>An error has occurred while trying to convert files. We could not convert</red> <green>%file%</green> <red>so please check the console.</red>";

    @Key("command.convert.successfully-converted-files")
    public static String SUCCESSFULLY_CONVERTED_FILES = "<green>Plugin Conversion has succeeded!</green>";

    @Key("command.reload.confirm-reload")
    public static String CONFIRM_RELOAD = "<yellow>Are you sure you want to reload the plugin?</yellow>";

    @Key("command.reload.reload-completed")
    public static String RELOAD_COMPLETED = "<green>Plugin reload has been completed.</green>";

    @Key("command.transfer.not-enough-keys")
    public static String TRANSFER_NOT_ENOUGH_KEYS = "<red>You do not have enough keys to transfer.</red>";

    @Key("command.transfer.transferred-keys")
    public static String TRANSFERRED_KEYS = "<gray>You have transferred</gray> <green>%amount% %crate%</green> <gray>keys to</gray> <red>%player%.</red>";

    @Key("command.transfer.transferred-keys-received")
    public static String TRANSFERRED_KEYS_RECEIVED = "<gray>You have received</gray> <green>%amount% %crate%</green> <gray>keys from</gray> <red>%player%.</red>";

    @Key("command.keys.personal.no-virtual-keys")
    public static String KEYS_PERSONAL_NO_VIRTUAL_KEYS = "<dark_gray>(</dark_gray><red>!</red><dark_gray>)</dark_gray> <gray>You currently do not have any virtual keys.</gray>";

    @Key("command.keys.personal.virtual-keys-header")
    public static List<String> KEYS_PERSONAL_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("<dark_gray>(</dark_gray><red>!</red><dark_gray>)</dark_gray> <gray>A list of your current amount of keys.</gray>");
    }};

    @Key("command.keys.other-player.no-virtual-keys")
    public static String KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS = "<dark_gray>(</dark_gray><red>!</red><dark_gray>)</dark_gray> <gray>The player</gray> <red>%player%</red> <gray>does not have any keys.</gray>";

    @Key("command.keys.other-player.virtual-keys-header")
    public static List<String> KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("<dark_gray>(</dark_gray><red>!</red><dark_gray>)</dark_gray> <gray>A list of</gray> <red>%player%''s</red> <gray>current amount of keys.</gray>");
    }};

    @Key("command.keys.crate-format")
    public static String CRATE_FORMAT = "%crate% <dark_gray>»</dark_gray>»</dark_gray> <gold>%keys% keys.</gold>";

    @Key("command.player-help")
    public static List<String> PLAYER_HELP = new ArrayList<>() {{
        add(" <dark_green>Crazy Crates Player Help!</dark_green>");
        add(" ");
        add(" <dark_gray>»</dark_gray></dark_gray> <gold>/key [player]</gold> <gray>»</gray> <yellow>Check how many keys a player has.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc</gold> <gray>»</gray> <yellow>Opens the crate menu.</yellow>");
    }};

    @Key("command.admin-help")
    public static List<String> ADMIN_HELP = new ArrayList<>() {{
        add(" <red>Crazy Crates Admin Help</red>");
        add(" ");
        add(" <dark_gray>»</dark_gray> <gold>/cc additem [crate] [prize]</gold> <gray>-</gray> <yellow>Add items in-game to a prize in a crate.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc preview [crate] [player]</gold> <gray>-</gray> <yellow>Opens the preview of a crate for a player.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc list</gold> <gray>-</gray> <yellow>Lists all crates.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc open [crate] [player]</gold> <gray>-</gray> <yellow>Tries to open a crate for a player if they have a key.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc forceopen [crate] [player]</gold> <gray>-</gray> <yellow>Opens a crate for a player for free.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc tp [location]</gold> <gray>-</gray> <yellow>Teleport to a Crate.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc give [physical/virtual] [crate] [amount] [player]</gold> <gray>-</gray> <yellow>Allows you to take keys from a player.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc set [crate]</gold> <gray>-</gray> <yellow>Set the block you are looking at as a crate.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc set Menu</gold> <gray>-</gray> <yellow>Set the block you are looking at to open the</yellow> <red>/cc menu.</red>");
        add(" <dark_gray>»</dark_gray> <gold>/cc reload</gold> <gray>-</gray> <yellow>Reloads the config/data files.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc set1/set2</gold> <gray>-</gray> <yellow>Sets position <red>#1 or #2</red> for when making a new schematic for QuadCrates.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc save [file name]</gold> <gray>-</gray> <yellow>Create a new nbt file in the schematics folder.</yellow>");
        add(" ");
        add(" <dark_gray>»</dark_gray> <gold>/key [player]</gold> <gray>-</gray> <yellow>Check how many keys a player has.</yellow>");
        add(" <dark_gray>»</dark_gray> <gold>/cc</gold> <gray>-</gray> <yellow>Opens the crate menu.</yellow>");
        add(" ");
        add("<gray>You can find a list of permissions @</gray> <yellow>https://github.com/badbones69/Crazy-Crates/wiki/Commands-and-Permissions</yellow>");
    }};

    private static final LocaleFile LOCALE_FILE = new LocaleFile();

    public static void reload(Path path, String fileName, CrazyCrates plugin) {
        LOCALE_FILE.handle(path.resolve("/locale/" + fileName), LocaleFile.class, plugin);
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