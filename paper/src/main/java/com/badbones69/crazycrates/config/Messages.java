package com.badbones69.crazycrates.config;

import com.badbones69.crazycrates.api.files.AbstractConfig;
import com.badbones69.crazycrates.api.files.annotations.Key;
import com.badbones69.crazycrates.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class Messages extends AbstractConfig {

    @Key("Messages.No-Teleporting")
    public String noTeleporting = "%prefix%&cYou may not teleport away while opening a Crate.";

    @Key("Messages.No-Commands-While-In-Crate")
    public String noCommandsWhileInCrate = "%prefix%&cYou are not allowed to use commands while opening Crates.";

    @Key("Messages.No-Key")
    public String noKey = "%prefix%&cYou must have a %key% &cin your hand to use that Crate.";

    @Key("Messages.No-Virtual-Key")
    public String noVirtualKey = "%prefix%&cYou need a key to open that Crate.";

    @Key("Messages.Not-On-Block")
    public String notOnBlock = "%prefix%&cYou must be standing on a block to use this Crate.";

    @Key("Messages.Already-Opening-Crate")
    public String alreadyOpeningCrate = "%prefix%&cYou are already opening a Crate.";

    @Key("Messages.Quick-Crate-In-Use")
    public String quickCrateInUse = "%prefix%&cThat Crate is already in use. Please wait for the Crate to open up.";

    @Key("Messages.World-Disabled")
    public String worldDisabled = "%prefix%&cI am sorry but Crates are disabled in %world%.";

    @Key("Messages.Reload")
    public String reload = "%prefix%&3You have reloaded the Config and Data Files.";

    @Key("Messages.Not-Online")
    public String notOnline = "%prefix%&cThe player &6%player% &cis not online.";

    @Key("Messages.No-Permission")
    public String noPermission = "%prefix%&cYou do not have permission to use that command!";

    @Key("Messages.Crate-Already-Opened")
    public String crateAlreadyOpened = "%prefix%&cYou are already opening a Crate.";

    @Key("Messages.Cant-Be-A-Virtual-Crate")
    public String cantBeVirtualCrate = "%prefix%&cThat Crate type cannot be used as a Virtual Crate.";

    @Key("Messages.Inventory-Full")
    public String inventoryFull = "%prefix%&cYour inventory is full, Please make room before opening a Crate.";

    @Key("Messages.To-Close-To-Another-Player")
    public String toCloseToAnotherPlayer = "%prefix%&cYou are too close to a player that is opening their Crate.";

    @Key("Messages.Needs-More-Room")
    public String needsMoreRoom = "%prefix%&cThere is not enough space to open that here.";

    @Key("Messages.Out-Of-Time")
    public String outOfTime = "%prefix%&cYou took 5 Minutes to open the Crate so it closed.";

    @Key("Messages.Must-Be-A-Player")
    public String mustBePlayer = "%prefix%&cYou must be a player to use this command.";

    @Key("Messages.Must-Be-A-Console-Sender")
    public String mustBeConsoleSender = "%prefix%&cYou must be using console to use this command.";

    @Key("Messages.Must-Be-Looking-At-A-Block")
    public String mustBeLookingAtBlock = "%prefix%&cYou must be looking at a block.";

    @Key("Messages.Not-A-Crate")
    public String notACrate = "%prefix%&cThere is no crate called &6%crate%.";

    @Key("Messages.Not-A-Number")
    public String notANumber = "%prefix%&6%number% &cis not a number.";

    @Key("Messages.Given-A-Player-Keys")
    public String givenPlayerKeys = "%prefix%&7You have given &6%player% %amount% &7Keys.";

    @Key("Messages.Obtaining-Keys")
    public String obtainingKeys = "%prefix%&7You have been given &6%amount% %key% &7Keys.";

    @Key("Messages.Given-Everyone-Keys")
    public String givenEveryoneKeys = "%prefix%&7You have given everyone &6%amount% &7Keys.";

    @Key("Messages.Given-Offline-Player-Keys")
    public String givenOfflinePlayerKeys = "%prefix%&7You have given &6%amount% &7key(s) to the offline player &6%player%.";

    @Key("Messages.Take-A-Player-Keys")
    public String takePlayerKeys = "%prefix%&7You have taken &6%amount% &7key(s) from &6%player%.";

    @Key("Messages.Take-Offline-Player-Keys")
    public String takeOfflinePlayerKeys = "%prefix%&7You have taken &6%amount% &7key(s) from the offline player &6%player%.";

    @Key("Messages.Opened-A-Crate")
    public String openedCrate = "%prefix%&7You have just opened the &6%crate% &7crate for &6%player%.";

    @Key("Messages.Internal-Error")
    public String internalError = "%prefix%&cAn internal error has occurred. Please check the console for the full error.";

    @Key("Messages.Unknown-Command")
    public String unknownCommand = "%prefix%&cThis command is not known.";

    @Key("Messages.Too-Many-Args")
    public String tooManyArgs = "%prefix%&cYou put more arguments then I can handle.";

    @Key("Messages.Not-Enough-Args")
    public String notEnoughArgs = "%prefix%&cYou did not supply enough arguments.";

    @Key("Messages.No-Item-In-Hand")
    public String noItemInHand = "%prefix%&cYou need to have an item in your hand to add it to the Crate.";

    @Key("Messages.Added-Item-With-Editor")
    public String itemWithEditor = "%prefix%&7The item has been added to the %crate% Crate in prize #%prize%.";

    @Key("Messages.Preview-Disabled")
    public String previewDisabled = "%prefix%&cThe preview for that crate is currently disabled.";

    @Key("Messages.No-Schematics-Found")
    public String noSchematicsFound = "%prefix%&cNo schematic were found. Please make sure NBT files exist in the schematics folder if not delete the folder to regenerate.";

    @Key("Messages.Same-Player")
    public String samePlayer = "%prefix%&cYou can''t use this command on yourself.";

    @Key("Messages.Prize-Error")
    public String prizeError = "%prefix%&cAn error has occurred while trying to give you the prize called &6%prize%&c in crate called &6%crate%&c. Please contact the server owner and show them this error.";

    @Key("Messages.Not-Enough-Keys")
    public String notEnoughKeys = "%prefix%&cYou do not have enough keys to transfer.";

    @Key("Messages.Transferred-Keys")
    public String transferredKeys = "%prefix%&7You have transferred %amount% %crate% keys to %player%.";

    @Key("Messages.ReceivedTransferred-Keys")
    public String receivedTransferredKeys = "%prefix%&7You have received %amount% %crate% keys from %player%.";

    @Key("Messages.Files-Converted.No-Files-To-Convert")
    public String noFilesToConvert = "%prefix%&cNo plugins that can be converted were found.";

    @Key("Messages.Files-Converted.Error-Converting-Files")
    public String errorConvertingFiles = "%prefix%&cThere was an error while trying to convert files. Please check console for the error log.";

    @Key("Messages.Created-Physical-Crate")
    public List<String> createdPhysicalCrate = new ArrayList<>() {{
        add("%prefix%&7You have set that block to %crate%.");
        add("&7To remove the crate shift break in creative to remove.");
    }};

    @Key("Messages.Removed-Physical-Crate")
    public String removedPhysicalCrate = "&7You have removed &6%id%.";

    @Key("Messages.Keys.Personal.No-Virtual-Keys")
    public String noVirtualKeys = "&8&l(&4&l!&8&l) &7You currently do not have any virtual keys.";

    @Key("Messages.Keys.Personal.Header")
    public List<String> noVirtualKeysHeader = new ArrayList<>() {{
        add("&8&l(&6&l!&8&l) &7List of your current number of keys.");
    }};

    @Key("Messages.Keys.Other-Player.No-Virtual-Keys")
    public String otherPlayerNoVirtualKeys = "&8&l(&4&l!&8&l) &7The player %player% does not have any keys.";

    @Key("Messages.Keys.Other-Player.Header")
    public List<String> otherPlayerNoKeysHeader = new ArrayList<>() {{
        add("&8&l(&6&l!&8&l) &7List of %player%''s current number of keys.");
    }};

    @Key("Messages.Keys.Per-Crate")
    public String perCrateKeys = "%crate% &7&l>&8&l> &6%keys% keys";

    @Key("Messages.Help")
    public List<String> playerHelp = new ArrayList<>() {{
        add("&e&lCrazy Crates Player Help");
        add("&6/key [player] &7- &eCheck the number of keys a player has.");
        add("&6/cc &7- &eOpens the menu.");
    }};

    @Key("Messages.Admin-Help")
    public List<String> adminHelp = new ArrayList<>() {{
        add("&c&lCrazy Crates Admin Help");
        add(" ");
        add("&6/cc additem <crate> <prize> &7- &eAdd items in-game to a prize in a crate.");
        add("&6/cc preview <crate> [player] &7- &eOpens the preview of a crate for a player.");
        add("&6/cc list &7- &eLists all crates.");
        add("&6/cc open <crate> [player] &7- &eTries to open a crate for a player if they have a key.");
        add("&6/cc forceopen <crate> [player] &7- &eOpens a crate for a player for free.");
        add("&6/cc tp <location> &7- &eTeleport to a Crate.");
        add("&6/cc give <physical/virtual> <crate> [amount] [player] &7- &eAllows you to take keys from a player.");
        add("&6/cc set <crate> &7- &eSet the block you are looking at as a crate.");
        add("&6/cc set Menu &7- &eSet the block you are looking at to open the /cc menu.");
        add("&6/cc reload &7- &eReloads the config/data files.");
        add("&6/cc set1/set2 &7- &eSets position &c#1 &eor &c#2 for when making a new schematic for QuadCrates.");
        add("&6/cc save <file name> &7- &eCreate a new nbt file in the schematics folder.");
        add(" ");
        add("&6/key [player] &7- &eCheck the number of keys a player has.");
        add("&6/cc &7- &eOpens the menu.");
        add(" ");
        add("&7You can find a list of permissions @ &ehttps://github.com/badbones69/Crazy-Crates/wiki/Commands-and-Permissions");
    }};

    public void reload() {
        save(FileUtils.INSTANCE.getDataFolder().resolve("messages.yml"), this);
    }
}