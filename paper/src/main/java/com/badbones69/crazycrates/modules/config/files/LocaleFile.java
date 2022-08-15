package com.badbones69.crazycrates.modules.config.files;

import com.badbones69.crazycrates.modules.config.AbstractConfig;
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
public class LocaleFile extends AbstractConfig {

    @Key("prefix.logger")
    public String PREFIX_LOGGER = "<gray>[<gold>CrazyCrates</gold>]</gray>";

    @Key("prefix.command")
    public String PREFIX_COMMAND = "<white>[<gradient:#FE5F55:#392F5A>CrazyCrew</gradient>]</white>";

    // Misc
    @Key("misc.unknown-command")
    public String UNKNOWN_COMMAND = "&cThis command is not known.";

    @Key("misc.no-teleporting")
    public String NO_TELEPORTING = "&cYou may not teleport away while opening&r %crate%.";

    @Key("misc.no-commands")
    public String NO_COMMANDS = "&cYou are not allowed to use commands while opening&r %crate%.";

    @Key("misc.no-keys")
    public String NO_KEYS = "&cYou need a&r %key% &cin your hand to use %crate%.";

    @Key("misc.no-virtual-keys")
    public String NO_VIRTUAL_KEYS = "&cYou need&r %key% &cto open %crate%.";

    // Errors
    @Key("errors.no-prizes-found")
    public String NO_PRIZES_FOUND = "&cThis crate contains no prizes that you can win.";

    @Key("errors.no-schematics-found")
    public String NO_SCHEMATICS_FOUND = "&cNo schematic were found, Please re-generate them by deleting the folder or checking for errors!";

    @Key("errors.prize-error")
    public List<String> PRIZE_ERROR = new ArrayList<>() {{
        add("&cAn error has occurred while trying to give you the prize&r %prize%.");
        add("&eThis has occurred in&r %crate%. Please notify your owner.");
    }};

    @Key("errors.internal-error")
    public String INTERNAL_ERROR = "&cAn internal error has occurred. Please check the console for the full error.";

    // Player handling
    @Key("player.requirements.too-many-args")
    public String TOO_MANY_ARGS = "&cYou put more arguments then I can handle.";

    @Key("player.requirements.not-enough-args")
    public String NOT_ENOUGH_ARGs = "&cYou did not supply enough arguments.";

    @Key("player.requirements.must-be-player")
    public String MUST_BE_PLAYER = "&cYou must be a player to use this command.";

    @Key("player.requirements.must-be-console-sender")
    public String MUST_BE_CONSOLE_SENDER = "&cYou must be using console to use this command.";

    @Key("player.requirements.must-be-looking-at-block")
    public String MUST_BE_LOOKING_AT_BLOCK = "&cYou must be looking at a block.";

    @Key("player.target-not-online")
    public String TARGET_NOT_ONLINE = "&cThe player&r &6%player% &cis not online.";

    @Key("player.target-same-player")
    public String TARGET_SAME_PLAYER = "&cYou cannot use this command on yourself.";

    @Key("player.no-permission")
    public String NO_PERMISSION = "&cYou do not have permission to use that command!";

    @Key("player.inventory-not-empty")
    public String INVENTORY_NOT_EMPTY = "&cInventory is not empty, Please make room before opening&r %crate%.";

    @Key("player.obtaining-keys")
    public String OBTAINING_KEYS = "&7You have been given &6%amount% %key% &7Keys.";

    @Key("player.too-close-to-another-player")
    public String TOO_CLOSE_TO_ANOTHER_PLAYER = "&cYou are too close to a player that is opening their Crate.";

    // Crates
    @Key("crates.requirements.not-a-crate")
    public String NOT_A_CRATE = "&cThere is no crate called&r %crate%.";

    @Key("crates.requirements.not-a-number")
    public String NOT_A_NUMBER = "&6%number% &cis not a number.";

    @Key("crates.not-a-block")
    public String NOT_A_BLOCK = "&cYou must be standing on a block to use&r %crate%.";

    @Key("crates.out-of-time")
    public String OUT_OF_TIME = "&cYou took &a5 Minutes &cto open the&r %crate% &cso it closed.";

    @Key("crates.crate-preview-disabled")
    public String CRATE_PREVIEW_DISABLED = "&cThe preview for&r %crate% &7is currently disabled.";

    @Key("crates.crate-already-open")
    public String CRATES_ALREADY_OPEN = "&cYou are already opening&r %crate%.";

    @Key("crates.crate-in-use")
    public String CRATES_IN_USE = "%crate% &cis already in use. Please wait until it finishes!";

    @Key("crates.cannot-be-a-virtual-crate")
    public String CANNOT_BE_A_VIRTUAL_CRATE = "&c%crate% cannot be used as a Virtual Crate. You have it set to&r %cratetype%";

    @Key("crates.need-more-room")
    public String NEED_MORE_ROOM = "&cThere is not enough space to open that here.";

    @Key("crates.world-disabled")
    public String WORLD_DISABLED = "&cCrates are disabled in &a%world%.";

    @Key("crates.physical-crate.created")
    public List<String> PHYSICAL_CRATE_CREATED = new ArrayList<>() {{
        add("&7You have set that block to&r %crate%.");
        add("&7To remove&r %crate%, &7Shift Click Break in Creative to remove.");
    }};

    @Key("crates.physical-crate.removed")
    public String PHYSICAL_CRATE_REMOVED = "&7You have removed &6%id%.";

    // Commands
    @Key("command.open.opened-a-crate")
    public String OPENED_A_CRAtE = "&7You have opened the&r %crate% &7crate for &6%player%.";

    @Key("command.give.given-player-keys")
    public String GIVEN_PLAYER_KEYS = "&7You have given &6%player% %amount% &7Keys.";

    @Key("command.give.given-everyone-keys")
    public String GIVEN_EVERYONE_KEYS = "&7You have given everyone &6%amount% &7Keys.";

    @Key("command.given-offline-player-keys")
    public String GIVEN_OFFLINE_PLAYER_KEYS = "&7You have given &6%amount% &7key(s) to the offline player &6%player%.";

    @Key("command.take.take-player-keys")
    public String TAKE_PLAYER_KEYS = "&7You have taken &6%amount% &7key(s) from &6%player%.";

    @Key("command.take.take-offline-player-keys")
    public String TAKE_OFFLINE_PLAYER_KEYS = "&7You have taken &6%amount% &7key(s) from the offline player &6%player%.";

    @Key("command.additem.no-item-in-hand")
    public String NO_ITEM_IN_HAND = "&cYou need to have an item in your hand to add it to&r %crate%.";

    @Key("command.additem.add-item-from-hand")
    public String ADD_ITEM_FROM_HAND = "&7The item has been added to&r %crate% as Prize #%prize%.";

    @Key("command.convert.no-files-to-convert")
    public String NO_FILES_TO_CONVERT = "&cNo available plugins to convert files.";

    @Key("command.convert.error-converting-files")
    public String ERROR_CONVERTING_FILES = "&cAn error has occurred while trying to convert files. We could not convert &a%file% &cso please check the console.";

    @Key("command.convert.successfully-converted-files")
    public String SUCCESSFULLY_CONVERTED_FILES = "&aPlugin Conversion has succeeded!";

    @Key("command.reload.confirm-reload")
    public String CONFIRM_RELOAD = "&eAre you sure you want to reload the plugin?";

    @Key("command.reload.reload-completed")
    public String RELOAD_COMPLETED = "&aPlugin reload has been completed.";

    @Key("command.transfer.not-enough-keys")
    public String TRANSFER_NOT_ENOUGH_KEYS = "&cYou do not have enough keys to transfer.";

    @Key("command.transfer.transferred-keys")
    public String TRANSFERRED_KEYS = "&7You have transferred &a%amount% %crate% &7keys to &c%player%.";

    @Key("command.transfer.transferred-keys-received")
    public String TRANSFERRED_KEYS_RECEIVED = "&7You have received &a%amount%&r %crate% &7keys from &c%player%.";

    @Key("command.keys.personal.no-virtual-keys")
    public String KEYS_PERSONAL_NO_VIRTUAL_KEYS = "&8(&c!&8) &7You currently do not have any virtual keys.";

    @Key("command.keys.personal.virtual-keys-header")
    public List<String> KEYS_PERSONAL_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("&8(&6!&8) &7A list of your current amount of keys.");
    }};

    @Key("command.keys.other-player.no-virtual-keys")
    public String KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS = "&8(&c!&8) &7The player &c%player% &7does not have any keys.";

    @Key("command.keys.other-player.virtual-keys-header")
    public List<String> KEYS_OTHER_PLAYER_NO_VIRTUAL_KEYS_HEADER = new ArrayList<>() {{
        add("&8(&6!&8) &7A list of &c%player%''s &7current amount of keys.");
    }};

    @Key("command.keys.crate-format")
    public String CRATE_FORMAT = "%crate% &8>> &6%keys% keys.";

    @Key("command.player-help")
    public List<String> PLAYER_HELP = new ArrayList<>() {{
        add("&r &2Crazy Crates Player Help!");
        add(" ");
        add("&8> &6/key [player] &7- &eCheck how many keys a player has.");
        add("&8> &6/cc &7- &eOpens the crate menu.");
    }};

    @Key("command.admin-help")
    public List<String> ADMIN_HELP = new ArrayList<>() {{
        add("&r &cCrazy Crates Admin Help");
        add(" ");
        add("&8> &6/cc additem <crate> <prize> &7- &eAdd items in-game to a prize in a crate.");
        add("&8> &6/cc preview <crate> [player] &7- &eOpens the preview of a crate for a player.");
        add("&8> &6/cc list &7- &eLists all crates.");
        add("&8> &6/cc open <crate> [player] &7- &eTries to open a crate for a player if they have a key.");
        add("&8> &6/cc forceopen <crate> [player] &7- &eOpens a crate for a player for free.");
        add("&8> &6/cc tp <location> &7- &eTeleport to a Crate.");
        add("&8> &6/cc give <physical/virtual> <crate> [amount] [player] &7- &eAllows you to take keys from a player.");
        add("&8> &6/cc set <crate> &7- &eSet the block you are looking at as a crate.");
        add("&8> &6/cc set Menu &7- &eSet the block you are looking at to open the /cc menu.");
        add("&8> &6/cc reload &7- &eReloads the config/data files.");
        add("&8> &6/cc set1/set2 &7- &eSets position &c#1 &eor &c#2 for when making a new schematic for QuadCrates.");
        add("&8> &6/cc save <file name> &7- &eCreate a new nbt file in the schematics folder.");
        add(" ");
        add("&8> &6/key [player] &7- &eCheck how many keys a player has.");
        add("&8> &6/cc &7- &eOpens the crate menu.");
        add(" ");
        add("&7You can find a list of permissions @ &ehttps://github.com/badbones69/Crazy-Crates/wiki/Commands-and-Permissions");
    }};

    public void reload(Path path, String fileName) {
        this.reload(path.resolve("/locale/" + fileName), LocaleFile.class);
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