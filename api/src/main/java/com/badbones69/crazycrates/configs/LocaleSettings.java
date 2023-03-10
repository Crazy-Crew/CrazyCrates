package com.badbones69.crazycrates.configs;

import us.crazycrew.crazycore.files.FileExtension;
import us.crazycrew.crazycore.files.FileManager;
import us.crazycrew.crazycore.files.annotations.Header;
import us.crazycrew.crazycore.files.annotations.Path;
import us.crazycrew.crazycore.files.enums.FileType;
import us.crazycrew.crazycore.utils.FileUtils;

@Header("""
        If you notice any translation issues, Do not hesitate to contact our Translators.
        
        Discord: https://discord.gg/crazycrew
        Github: https://github.com/Crazy-Crew
        
        Report Issues: https://github.com/Crazy-Crew/CrazyCrates/issues
        Request Features/Support: https://github.com/orgs/Crazy-Crew/discussions
        """)
public class LocaleSettings extends FileExtension {

    // Misc
    @Path("misc.unknown-command")
    public static String UNKNOWN_COMMAND = "<red>This command is not known.</red>";

    @Path("misc.no-teleporting")
    public static String NO_TELEPORTING = "<red>You may not teleport away while opening</red> <gold>%crate%.</gold>";

    @Path("misc.no-commands")
    public static String NO_COMMANDS = "<red>You are not allowed to use commands while opening</red> <gold>%crate%.</gold>";

    @Path("misc.no-keys")
    public static String NO_KEYS = "<red>You need a</red> %key% <red>in your hand to use</red> <gold>%crate%.</gold>";

    @Path("misc.no-virtual-keys")
    public static String NO_VIRTUAL_KEYS = "<red>You need</red> %key% <red>to open</red> <gold>%crate%.</gold>";
    
    @Path("misc.feature-disabled")
    public static String FEATURE_DISABLED = "<red>This feature is disabled. We have no ETA on when this will function.</red>";
    
    @Path("misc.correct-usage")
    public static String CORRECT_USAGE = "<red>The correct usage for this command is</red> <yellow>%usage%</yellow>";

    // Errors
    @Path("errors.no-prizes-found")
    public static String NO_PRIZES_FOUND = "<red>This crate contains no prizes that you can win.</red>";

    @Path("errors.no-schematics-found")
    public static String NO_SCHEMATICS_FOUND = "<red>No schematic were found, Please re-generate them by deleting the folder or checking for errors!</red>";

    @Path("errors.prize-error")
    public static String PRIZE_ERROR = """
            <red>An error has occurred while trying to give you the prize</red> <gold>%prize%.</gold>
            <yellow>This has occurred in</yellow> <gold>%crate%.</gold> <yellow>Please notify your owner.</yellow>
            """;

    @Path("errors.internal-error")
    public static String INTERNAL_ERROR = "<red>An internal error has occurred. Please check the console for the full error.</red>";

    // Player handling
    @Path("player.requirements.too-many-args")
    public static String TOO_MANY_ARGS = "<red>You put more arguments then I can handle.</red>";

    @Path("player.requirements.not-enough-args")
    public static String NOT_ENOUGH_ARGs = "<red>You did not supply enough arguments.</red>";

    @Path("player.requirements.must-be-player")
    public static String MUST_BE_PLAYER = "<red>You must be a player to use this command.</red>";

    @Path("player.requirements.must-be-console-sender")
    public static String MUST_BE_CONSOLE_SENDER = "<red>You must be using console to use this command.</red>";

    @Path("player.requirements.must-be-looking-at-block")
    public static String MUST_BE_LOOKING_AT_BLOCK = "<red>You must be looking at a block.</red>";

    @Path("player.target-not-online")
    public static String TARGET_NOT_ONLINE = "<red>The player</red> <gold>%player%</gold> <red>is not online.</red>";

    @Path("player.target-same-player")
    public static String TARGET_SAME_PLAYER = "<red>You cannot use this command on yourself.</red>";

    @Path("player.no-permission")
    public static String NO_PERMISSION = "<red>You do not have permission to use that command!</red>";

    @Path("player.inventory-not-empty")
    public static String INVENTORY_NOT_EMPTY = "<red>Inventory is not empty, Please make room before opening</red> <gold>%crate%.</gold>";

    @Path("player.obtaining-keys")
    public static String OBTAINING_KEYS = "<gray>You have been given</gray> <gold>%amount% %key%</gold> <gray>Keys.</gray>";

    @Path("player.too-close-to-another-player")
    public static String TOO_CLOSE_TO_ANOTHER_PLAYER = "<red>You are too close to a player that is opening their Crate.</red>";

    // Crates
    @Path("crates.requirements.not-a-crate")
    public static String NOT_A_CRATE = "<red>There is no crate called</red> <gold>%crate%.</gold>";

    @Path("crates.requirements.not-a-number")
    public static String NOT_A_NUMBER = "<gold>%number%</gold> <red>is not a number.</red>";

    @Path("crates.not-a-block")
    public static String NOT_A_BLOCK = "<red>You must be standing on a block to use</red> <gold>%crate%.</gold>";

    @Path("crates.out-of-time")
    public static String OUT_OF_TIME = "<red>You took</red> <green>5 Minutes</green> <red>to open the</red> <gold>%crate%</gold> <red>so it closed.</red>";

    @Path("crates.crate-preview-disabled")
    public static String CRATE_PREVIEW_DISABLED = "<red>The preview for</red> <gold>%crate%</gold> <red>is currently disabled.</red>";

    @Path("crates.crate-already-open")
    public static String CRATES_ALREADY_OPEN = "<red>You are already opening</red> <gold>%crate%.</gold>";

    @Path("crates.crate-in-use")
    public static String CRATES_IN_USE = "<gold>%crate%</gold> <red>is already in use. Please wait until it finishes!</red>";

    @Path("crates.cannot-be-a-virtual-crate")
    public static String CANNOT_BE_A_VIRTUAL_CRATE = "<gold>%crate%</gold> <red>cannot be used as a Virtual Crate. You have it set to</red> <gold>%cratetype%.</gold>";

    @Path("crates.need-more-room")
    public static String NEED_MORE_ROOM = "<red>There is not enough space to open that here.</red>";

    @Path("crates.world-disabled")
    public static String WORLD_DISABLED = "<red>Crates are disabled in</red> <green>%world%.</green>";

    @Path("crates.physical-crate.created")
    public static String PHYSICAL_CRATE_CREATED = """
        <gray>You have set that block to</gray> <gold>%crate%.</gold>
        <gray>To remove</gray> <gold>%crate%</gold>, <gray>Shift Click Break in Creative to remove.</gray>
        """;

    @Path("crates.physical-crate.removed")
    public static String PHYSICAL_CRATE_REMOVED = "<gray>You have removed</gray> <gold>%id%.</gold>";

    // Commands
    @Path("command.open.opened-a-crate")
    public static String OPENED_A_CRAtE = "<gray>You have opened the</gray> <gold>%crate%</gold> <gray>crate for</gray> <gold>%player%.</gold>";

    @Path("command.give.given-player-keys")
    public static String GIVEN_PLAYER_KEYS = "<gray>You have given</gray> <gold>%player% %amount% Keys.</gold>";

    @Path("command.give.given-everyone-keys")
    public static String GIVEN_EVERYONE_KEYS = "<gray>You have given everyone</gray> <gold>%amount% Keys.</gold>";

    @Path("command.give.given-offline-player-keys")
    public static String GIVEN_OFFLINE_PLAYER_KEYS = "<gray>You have given</gray> <gold>%amount% key(s)</gold> <gray>to the offline player</gray> <gold>%player%.</gold>";

    @Path("command.take.take-player-keys")
    public static String TAKE_PLAYER_KEYS = "<gray>You have taken</gray> <gold>%amount% key(s)</gold> <gray>from</gray> <gold>%player%.</gold>";

    @Path("command.take.take-offline-player-keys")
    public static String TAKE_OFFLINE_PLAYER_KEYS = "<gray>You have taken</gray> <gold>%amount% key(s)</gold> <gray>from the offline player</gray> <gold>%player%.</gold>";

    @Path("command.additem.no-item-in-hand")
    public static String NO_ITEM_IN_HAND = "<red>You need to have an item in your hand to add it to</red> <gold>%crate%.</gold>";

    @Path("command.additem.add-item-from-hand")
    public static String ADD_ITEM_FROM_HAND = "<gray>The item has been added to</gray> <gold>%crate%</gold> <gray>as</gray> <gold>Prize #%prize%.</gold>";

    @Path("command.convert.no-files-to-convert")
    public static String NO_FILES_TO_CONVERT = "<red>No available plugins to convert files.</red>";

    @Path("command.convert.error-converting-files")
    public static String ERROR_CONVERTING_FILES = "<red>An error has occurred while trying to convert files. We could not convert</red> <green>%file%</green> <red>so please check the console.</red>";

    @Path("command.convert.successfully-converted-files")
    public static String SUCCESSFULLY_CONVERTED_FILES = "</green>Plugin Conversion has succeeded!</green>";

    @Path("command.reload.confirm-reload")
    public static String CONFIRM_RELOAD = "<yellow>Are you sure you want to reload the plugin?</yellow>";

    @Path("command.reload.reload-completed")
    public static String RELOAD_COMPLETED = "<green>Plugin reload has been completed.</green>";

    @Path("command.transfer.not-enough-keys")
    public static String TRANSFER_NOT_ENOUGH_KEYS = "<red>You do not have enough keys to transfer.</red>";

    @Path("command.transfer.transferred-keys")
    public static String TRANSFERRED_KEYS = "<gray>You have transferred</gray> <green>%amount% %crate%</green> <gray>keys to</gray> <red>%player%.</red>";

    @Path("command.transfer.transferred-keys-received")
    public static String TRANSFERRED_KEYS_RECEIVED = "<gray>You have received</gray> <green>%amount% %crate%</green> <gray>keys from</gray> <red>%player%.</red>";

    @Path("command.keys.personal.no-virtual-keys")
    public static String KEYS_PERSONAL_NO_VIRTUAL_KEYS = "<dark_gray>(</dark_gray><red>!<dark_gray>)</dark_gray> <gray>You currently do not have any virtual keys.</gray>";

    @Path("command.keys.other-player.no-virtual-keys")
    public static String KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS = "<dark_gray>(</dark_gray><red>!</red><dark_gray>)</dark_gray> <gray>The player</gray> <red>%player%</red> <gray>does not have any keys.</gray>";

    @Path("command.keys.crate-format")
    public static String CRATE_FORMAT = """
            <dark_gray>(</dark_gray><red>!</red><dark_gray>)</dark_gray> <gray>A list of <red>%player%''s</red> current amount of keys.</gray>
            
            %crate% <dark_gray>»»</dark_gray> <gold>%keys% keys.</gold>""";

    @Path("command.player-help")
    public static String PLAYER_HELP = """
            <dark_green>Crazy Crates Player Help!</dark_green>
            
            <dark_gray>»</dark_gray> <gold>/key [player]</gold> <gray>-</gray> <yellow>Check how many keys a player has.</yellow>
            <dark_gray>»</dark_gray> <gold>/key</gold> <gray>-</gray> <yellow>Shows how many keys you have.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc</gold> <gray>-</gray> <yellow>Opens the crate menu.</yellow>
            """;

    @Path("command.admin-help")
    public static String ADMIN_HELP = """
            <red>Crazy Crates Admin Help</red>
            
            <dark_gray>»</dark_gray> <gold>/cc additem [crate] [prize]</gold> <gray>-</gray> <yellow>Add items in-game to a prize in a crate.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc preview [crate] %player%</gold> <gray>-</gray> <yellow>Opens the crate preview for player.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc list</gold> <gray>-</gray> <yellow>Lists all crates.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc open [crate] [player]</gold> <gray>-</gray> <yellow>Tries to open a crate for a player if they have a key.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc forceopen [crate] [player]</gold> <gray>-</gray> <yellow>Opens a crate for a player for free.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc tp [location]</gold> <gray>-</gray> <yellow>Teleport to a crate.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc give [physical/virtual] [crate] [amount] [player]</gold> <gray>-</gray> <yellow>Allows you to take keys from a player.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc set [crate]</gold> <gray>-</gray> <yellow>Set the block you are looking at as a crate.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc reload</gold> <gray>-</gray> <yellow>Reloads the config/data files.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc set1/set2</gold> <gray>-</gray> <yellow>Sets position <red>#1 or #2</red> for when making a new schematic for QuadCrates.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc save [file name]</gold> <gray>-</gray> <yellow>Create a new nbt file in the schematics folder.</yellow>
            
            <dark_gray>»</dark_gray> <gold>/key [player]</gold> <gray>-</gray> <yellow>Check how many keys a player has.</yellow>
            <dark_gray>»</dark_gray> <gold>/cc</gold> <gray>-</gray> <yellow>Opens the crate menu.</yellow>
            """;

    public LocaleSettings(java.nio.file.Path path) {
        super(PluginSettings.LOCALE_FILE, path.resolve("locale"), FileType.YAML);
    }

    public static void reload(FileManager fileManager, java.nio.file.Path directory) {
        FileUtils.extract("/locale", directory, false);

        fileManager.addFile(new LocaleSettings((directory)));
    }
}