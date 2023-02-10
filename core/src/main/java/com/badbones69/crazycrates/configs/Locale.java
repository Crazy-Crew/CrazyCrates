package com.badbones69.crazycrates.configs;

import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.annotations.yaml.Header;
import net.dehya.ruby.common.annotations.yaml.Key;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;
import net.dehya.ruby.files.FileManager;
import net.dehya.ruby.utils.FileUtils;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@FileBuilder(isLogging = true, isAsync = false, isData = false, fileType = FileType.YAML)
@Header("""
        If you notice any translation issues, Do not hesitate to contact our Translators.
        
        Discord: https://discord.gg/crazycrew
        Github: https://github.com/Crazy-Crew
        
        Report Issues: https://github.com/Crazy-Crew/CrazyCrates/issues
        Request Features/Support: https://github.com/orgs/Crazy-Crew/discussions
        """)
public class Locale extends FileExtension {

    // Misc
    @Key("misc.unknown-command")
    public static String UNKNOWN_COMMAND = "&cThis command is not known.";

    @Key("misc.no-teleporting")
    public static String NO_TELEPORTING = "&cYou may not teleport away while opening &6<crate>.";

    @Key("misc.no-commands")
    public static String NO_COMMANDS = "&cYou are not allowed to use commands while opening &6<crate>.";

    @Key("misc.no-keys")
    public static String NO_KEYS = "&cYou need a <key> &cin your hand to use &6<crate>.";

    @Key("misc.no-virtual-keys")
    public static String NO_VIRTUAL_KEYS = "&cYou need <key> &cto open &6<crate>.";
    
    @Key("misc.feature-disabled")
    public static String FEATURE_DISABLED = "&cThis feature is disabled. We have no ETA on when this will function.";
    
    @Key("misc.correct-usage")
    public static String CORRECT_USAGE = "&cThe correct usage for this command is &e<usage>";

    // Errors
    @Key("errors.no-prizes-found")
    public static String NO_PRIZES_FOUND = "&cThis crate contains no prizes that you can win.";

    @Key("errors.no-schematics-found")
    public static String NO_SCHEMATICS_FOUND = "&cNo schematic were found, Please re-generate them by deleting the folder or checking for errors!";

    @Key("errors.prize-error")
    public static List<String> PRIZE_ERROR = new ArrayList<>() {{
        add("&cAn error has occurred while trying to give you the prize &6<prize>.");
        add("&eThis has occurred in &6<crate>. &ePlease notify your owner.");
    }};

    @Key("errors.internal-error")
    public static String INTERNAL_ERROR = "&cAn internal error has occurred. Please check the console for the full error.";

    // Player handling
    @Key("player.requirements.too-many-args")
    public static String TOO_MANY_ARGS = "&cYou put more arguments then I can handle.";

    @Key("player.requirements.not-enough-args")
    public static String NOT_ENOUGH_ARGs = "&cYou did not supply enough arguments.";

    @Key("player.requirements.must-be-player")
    public static String MUST_BE_PLAYER = "&cYou must be a player to use this command.";

    @Key("player.requirements.must-be-console-sender")
    public static String MUST_BE_CONSOLE_SENDER = "&cYou must be using console to use this command.";

    @Key("player.requirements.must-be-looking-at-block")
    public static String MUST_BE_LOOKING_AT_BLOCK = "&cYou must be looking at a block.";

    @Key("player.target-not-online")
    public static String TARGET_NOT_ONLINE = "&cThe player &6<player> &cis not online.";

    @Key("player.target-same-player")
    public static String TARGET_SAME_PLAYER = "&cYou cannot use this command on yourself.";

    @Key("player.no-permission")
    public static String NO_PERMISSION = "&cYou do not have permission to use that command!";

    @Key("player.inventory-not-empty")
    public static String INVENTORY_NOT_EMPTY = "&cInventory is not empty, Please make room before opening &6<crate>.";

    @Key("player.obtaining-keys")
    public static String OBTAINING_KEYS = "&7You have been given &6<amount> <key> &7Keys.";

    @Key("player.too-close-to-another-player")
    public static String TOO_CLOSE_TO_ANOTHER_PLAYER = "&cYou are too close to a player that is opening their Crate.";

    // Crates
    @Key("crates.requirements.not-a-crate")
    public static String NOT_A_CRATE = "&cThere is no crate called &6<crate>.";

    @Key("crates.requirements.not-a-number")
    public static String NOT_A_NUMBER = "&6<number> &cis not a number.";

    @Key("crates.not-a-block")
    public static String NOT_A_BLOCK = "&cYou must be standing on a block to use &6<crate>.";

    @Key("crates.out-of-time")
    public static String OUT_OF_TIME = "&cYou took &a5 Minutes &cto open the &6<crate> &cso it closed.";

    @Key("crates.crate-preview-disabled")
    public static String CRATE_PREVIEW_DISABLED = "&cThe preview for &6<crate> &cis currently disabled.";

    @Key("crates.crate-already-open")
    public static String CRATES_ALREADY_OPEN = "&cYou are already opening &6<crate>.";

    @Key("crates.crate-in-use")
    public static String CRATES_IN_USE = "&6<crate> &cis already in use. Please wait until it finishes!";

    @Key("crates.cannot-be-a-virtual-crate")
    public static String CANNOT_BE_A_VIRTUAL_CRATE = "&6<crate> &ccannot be used as a Virtual Crate. You have it set to &6<cratetype>.";

    @Key("crates.need-more-room")
    public static String NEED_MORE_ROOM = "&cThere is not enough space to open that here.";

    @Key("crates.world-disabled")
    public static String WORLD_DISABLED = "&cCrates are disabled in &a<world>.";

    @Key("crates.physical-crate.created")
    public static List<String> PHYSICAL_CRATE_CREATED = new ArrayList<>() {{
        add("&7You have set that block to &6<crate>.");
        add("&7To remove &6<crate>, &7Shift Click Break in Creative to remove.");
    }};

    @Key("crates.physical-crate.removed")
    public static String PHYSICAL_CRATE_REMOVED = "&7You have removed &6<id>.";

    // Commands
    @Key("command.open.opened-a-crate")
    public static String OPENED_A_CRAtE = "&7You have opened the &6<crate> &7crate for &6<player>.";

    @Key("command.give.given-player-keys")
    public static String GIVEN_PLAYER_KEYS = "&7You have given &6<player> <amount> Keys.";

    @Key("command.give.given-everyone-keys")
    public static String GIVEN_EVERYONE_KEYS = "&7You have given everyone &6<amount> Keys.";

    @Key("command.give.given-offline-player-keys")
    public static String GIVEN_OFFLINE_PLAYER_KEYS = "&7You have given &6<amount> key(s) &7to the offline player &6<player>.";

    @Key("command.take.take-player-keys")
    public static String TAKE_PLAYER_KEYS = "&7You have taken &6<amount> key(s) &7from &6<player>.";

    @Key("command.take.take-offline-player-keys")
    public static String TAKE_OFFLINE_PLAYER_KEYS = "&7You have taken &6<amount> key(s) &7from the offline player &6<player>.";

    @Key("command.additem.no-item-in-hand")
    public static String NO_ITEM_IN_HAND = "&cYou need to have an item in your hand to add it to &6<crate>.";

    @Key("command.additem.add-item-from-hand")
    public static String ADD_ITEM_FROM_HAND = "&7The item has been added to &6<crate> &7as &6Prize #<prize>.";

    @Key("command.convert.no-files-to-convert")
    public static String NO_FILES_TO_CONVERT = "&cNo available plugins to convert files.";

    @Key("command.convert.error-converting-files")
    public static String ERROR_CONVERTING_FILES = "&cAn error has occurred while trying to convert files. We could not convert &a<file> &cso please check the console.";

    @Key("command.convert.successfully-converted-files")
    public static String SUCCESSFULLY_CONVERTED_FILES = "&aPlugin Conversion has succeeded!";

    @Key("command.reload.confirm-reload")
    public static String CONFIRM_RELOAD = "&eAre you sure you want to reload the plugin?";

    @Key("command.reload.reload-completed")
    public static String RELOAD_COMPLETED = "&aPlugin reload has been completed.";

    @Key("command.transfer.not-enough-keys")
    public static String TRANSFER_NOT_ENOUGH_KEYS = "&cYou do not have enough keys to transfer.";

    @Key("command.transfer.transferred-keys")
    public static String TRANSFERRED_KEYS = "&7You have transferred &a<amount> <crate> &7keys to &c<player>.";

    @Key("command.transfer.transferred-keys-received")
    public static String TRANSFERRED_KEYS_RECEIVED = "&7You have received &a<amount> <crate> &7keys from &c<player>.";

    @Key("command.keys.personal.no-virtual-keys")
    public static String KEYS_PERSONAL_NO_VIRTUAL_KEYS = "&8(&c!&8) &7You currently do not have any virtual keys.";

    @Key("command.keys.personal.virtual-keys-header")
    public static List<String> KEYS_PERSONAL_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("&8(&c!&8) &7A list of your current amount of keys.");
    }};

    @Key("command.keys.other-player.no-virtual-keys")
    public static String KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS = "&8(&c!&8) &7The player &c<player> &7does not have any keys.";

    @Key("command.keys.other-player.virtual-keys-header")
    public static List<String> KEYS_OTHER_PLAYER_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("&8(&c!&8) &7A list of &c<player>''s &7current amount of keys.");
    }};

    @Key("command.keys.crate-format")
    public static String CRATE_FORMAT = "<crate> &8»» &6<keys> keys.";

    @Key("command.player-help")
    public static String PLAYER_HELP = """
            &2Crazy Crates Player Help!\\n
            
            &8» &6/key [player] &7» &eCheck how many keys a player has.
            &8» &6/key &7- &eShows how many keys you have.
            &8» &6/cc &7» &eOpens the crate menu.
            """;

    @Key("command.admin-help")
    public static String ADMIN_HELP = """
            &c&lCrazy Crates Admin Help\\n
            
            &8» &6/cc additem [crate] [prize] &7- &eAdd items in-game to a prize in a crate.
            &8» &6/cc preview [crate] <player> &7- &eOpens the crate preview for player.
            &8» &6/cc list &7- &eLists all crates.
            &8» &6/cc open [crate] [player] &7- &eTries to open a crate for a player if they have a key.
            &8» &6/cc forceopen [crate] [player] &7- &eOpens a crate for a player for free.
            &8» &6/cc tp [location] &7- &eTeleport to a crate.
            &8» &6/cc give [physical/virtual] [crate] [amount] [player] &7- &eAllows you to take keys from a player.
            &8» &6/cc set [crate] &7- &eSet the block you are looking at as a crate.
            &8» &6/cc reload &7- &eReloads the config/data files.
            &8» &6/cc set1/set2 &7- &eSets position &c#1 or #2 for when making a new schematic for QuadCrates.
            &8» &6/cc save [file name] &7- &eCreate a new nbt file in the schematics folder.
            
            &8» &6/key [player] &7- &eCheck how many keys a player has.
            &8» &6/cc &7- &eOpens the crate menu.
            """;

    /**
     * A constructor to build our config file.
     * The path.resolve means it's looking for a locale directory instead of just the root directory.
     *
     * @param path the path of the file.
     */
    public Locale(Path path) {
        super(Config.LOCALE_FILE, path.resolve("locale"));
    }

    public static void reload(FileManager fileManager, Path directory) {
        FileUtils.extract("/locale", directory, false);

        fileManager.addFile(new Locale((directory)));
    }

    public static YamlConfiguration getConfiguration(FileManager fileManager, Path directory) {
        try {
            return fileManager.getFileConfiguration(new Locale(directory));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static File getConfig(FileManager fileManager, Path directory) {
        return fileManager.getFile(new Locale(directory));
    }
}