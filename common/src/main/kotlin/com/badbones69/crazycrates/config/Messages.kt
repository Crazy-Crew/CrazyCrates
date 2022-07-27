package com.badbones69.crazycrates.config

import com.badbones69.crazycrates.files.AbstractConfig
import com.badbones69.crazycrates.files.annotations.Key

object Messages : AbstractConfig() {

    @Key("Messages.No-Teleporting")
    val noTeleporting = "%prefix%&cYou may not teleport away while opening a Crate."

    @Key("Messages.No-Commands-While-In-Crate")
    val noCommandsWhileInCrate = "%prefix%&cYou are not allowed to use commands while opening Crates."

    @Key("Messages.No-Key")
    val noKey = "%prefix%&cYou must have a %key% &cin your hand to use that Crate."

    @Key("Messages.No-Virtual-Key")
    val noVirtualKey = "%prefix%&cYou need a key to open that Crate."

    @Key("Messages.Not-On-Block")
    val notOnBlock = "%prefix%&cYou must be standing on a block to use this Crate."

    @Key("Messages.Already-Opening-Crate")
    val alreadyOpeningCrate = "%prefix%&cYou are already opening a Crate."

    @Key("Messages.Quick-Crate-In-Use")
    val quickCrateInUse = "%prefix%&cThat Crate is already in use. Please wait for the Crate to open up."

    @Key("Messages.World-Disabled")
    val worldDisabled = "%prefix%&cI am sorry but Crates are disabled in %world%."

    @Key("Messages.Reload")
    val reload = "%prefix%&3You have reloaded the Config and Data Files."

    @Key("Messages.Not-Online")
    val notOnline = "%prefix%&cThe player &6%player% &cis not online."

    @Key("Messages.No-Permission")
    val noPermission = "%prefix%&cYou do not have permission to use that command!"

    @Key("Messages.Crate-Already-Opened")
    val crateAlreadyOpened = "%prefix%&cYou are already opening a Crate."

    @Key("Messages.Cant-Be-A-Virtual-Crate")
    val cantBeVirtualCrate = "%prefix%&cThat Crate type cannot be used as a Virtual Crate."

    @Key("Messages.Inventory-Full")
    val inventoryFull = "%prefix%&cYour inventory is full, Please make room before opening a Crate."

    @Key("Messages.To-Close-To-Another-Player")
    val toCloseToAnotherPlayer = "%prefix%&cYou are too close to a player that is opening their Crate."

    @Key("Messages.Needs-More-Room")
    val needsMoreRoom = "%prefix%&cThere is not enough space to open that here."

    @Key("Messages.Out-Of-Time")
    val outOfTime = "%prefix%&cYou took 5 Minutes to open the Crate so it closed."

    @Key("Messages.Must-Be-A-Player")
    val mustBePlayer = "%prefix%&cYou must be a player to use this command."

    @Key("Messages.Must-Be-A-Console-Sender")
    val mustBeConsoleSender = "%prefix%&cYou must be using console to use this command."

    @Key("Messages.Must-Be-Looking-At-A-Block")
    val mustBeLookingAtBlock = "%prefix%&cYou must be looking at a block."

    @Key("Messages.Not-A-Crate")
    val notACrate = "%prefix%&cThere is no crate called &6%crate%."

    @Key("Messages.Not-A-Number")
    val notANumber = "%prefix%&6%number% &cis not a number."

    @Key("Messages.Given-A-Player-Keys")
    val givenPlayerKeys = "%prefix%&7You have given &6%player% %amount% &7Keys."

    @Key("Messages.Obtaining-Keys")
    val obtainingKeys = "%prefix%&7You have been given &6%amount% %key% &7Keys."

    @Key("Messages.Given-Everyone-Keys")
    val givenEveryoneKeys = "%prefix%&7You have given everyone &6%amount% &7Keys."

    @Key("Messages.Given-Offline-Player-Keys")
    val givenOfflinePlayerKeys = "%prefix%&7You have given &6%amount% &7key(s) to the offline player &6%player%."

    @Key("Messages.Take-A-Player-Keys")
    val takePlayerKeys = "%prefix%&7You have taken &6%amount% &7key(s) from &6%player%."

    @Key("Messages.Take-Offline-Player-Keys")
    val takeOfflinePlayerKeys = "%prefix%&7You have taken &6%amount% &7key(s) from the offline player &6%player%."

    @Key("Messages.Opened-A-Crate")
    val openedCrate = "%prefix%&7You have just opened the &6%crate% &7crate for &6%player%."

    @Key("Messages.Internal-Error")
    val internalError = "%prefix%&cAn internal error has occurred. Please check the console for the full error."

    @Key("Messages.Unknown-Command")
    val unknownCommand = "%prefix%&cThis command is not known."

    @Key("Messages.Too-Many-Args")
    val tooManyArgs = "%prefix%&cYou put more arguments then I can handle."

    @Key("Messages.Not-Enough-Args")
    val notEnoughArgs = "%prefix%&cYou did not supply enough arguments."

    @Key("Messages.No-Item-In-Hand")
    val noItemInHand = "%prefix%&cYou need to have an item in your hand to add it to the Crate."

    @Key("Messages.Added-Item-With-Editor")
    val itemWithEditor = "%prefix%&7The item has been added to the %crate% Crate in prize #%prize%."

    @Key("Messages.Preview-Disabled")
    val previewDisabled = "%prefix%&cThe preview for that crate is currently disabled."

    @Key("Messages.No-Schematics-Found")
    val noSchematicsFound =
        "%prefix%&cNo schematic were found. Please make sure NBT files exist in the schematics folder if not delete the folder to regenerate."

    @Key("Messages.Same-Player")
    val samePlayer = "%prefix%&cYou can''t use this command on yourself."

    @Key("Messages.Prize-Error")
    val prizeError =
        "%prefix%&cAn error has occurred while trying to give you the prize called &6%prize%&c in crate called &6%crate%&c. Please contact the server owner and show them this error."

    @Key("Messages.Not-Enough-Keys")
    val notEnoughKeys = "%prefix%&cYou do not have enough keys to transfer."

    @Key("Messages.Transferred-Keys")
    val transferredKeys = "%prefix%&7You have transferred %amount% %crate% keys to %player%."

    @Key("Messages.ReceivedTransferred-Keys")
    val receivedTransferredKeys = "%prefix%&7You have received %amount% %crate% keys from %player%."

    @Key("Messages.Files-Converted.No-Files-To-Convert")
    val noFilesToConvert = "%prefix%&cNo plugins that can be converted were found."

    @Key("Messages.Files-Converted.Error-Converting-Files")
    val errorConvertingFiles =
        "%prefix%&cThere was an error while trying to convert files. Please check console for the error log."

    @Key("Messages.Created-Physical-Crate")
    val createdPhysicalCrate: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("%prefix%&7You have set that block to %crate%.")
            add("&7To remove the crate shift break in creative to remove.")
        }
    }

    @Key("Messages.Removed-Physical-Crate")
    val removedPhysicalCrate = "&7You have removed &6%id%."

    @Key("Messages.Keys.Personal.No-Virtual-Keys")
    val noVirtualKeys = "&8&l(&4&l!&8&l) &7You currently do not have any virtual keys."

    @Key("Messages.Keys.Personal.Header")
    val noVirtualKeysHeader: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("&8&l(&6&l!&8&l) &7List of your current number of keys.")
        }
    }

    @Key("Messages.Keys.Other-Player.No-Virtual-Keys")
    val otherPlayerNoVirtualKeys = "&8&l(&4&l!&8&l) &7The player %player% does not have any keys."

    @Key("Messages.Keys.Other-Player.Header")
    val otherPlayerNoKeysHeader: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("&8&l(&6&l!&8&l) &7List of %player%''s current number of keys.")
        }
    }

    @Key("Messages.Keys.Per-Crate")
    val perCrateKeys = "%crate% &7&l>&8&l> &6%keys% keys"

    @Key("Messages.Help")
    val playerHelp: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("&e&lCrazy Crates Player Help")
            add("&6/key [player] &7- &eCheck the number of keys a player has.")
            add("&6/cc &7- &eOpens the menu.")
        }
    }

    @Key("Messages.Admin-Help")
    val adminHelp: ArrayList<String?> = object : ArrayList<String?>() {
        init {
            add("&c&lCrazy Crates Admin Help")
            add(" ")
            add("&6/cc additem <crate> <prize> &7- &eAdd items in-game to a prize in a crate.")
            add("&6/cc preview <crate> [player] &7- &eOpens the preview of a crate for a player.")
            add("&6/cc list &7- &eLists all crates.")
            add("&6/cc open <crate> [player] &7- &eTries to open a crate for a player if they have a key.")
            add("&6/cc forceopen <crate> [player] &7- &eOpens a crate for a player for free.")
            add("&6/cc tp <location> &7- &eTeleport to a Crate.")
            add("&6/cc give <physical/virtual> <crate> [amount] [player] &7- &eAllows you to take keys from a player.")
            add("&6/cc set <crate> &7- &eSet the block you are looking at as a crate.")
            add("&6/cc set Menu &7- &eSet the block you are looking at to open the /cc menu.")
            add("&6/cc reload &7- &eReloads the config/data files.")
            add("&6/cc set1/set2 &7- &eSets position &c#1 &eor &c#2 for when making a new schematic for QuadCrates.")
            add("&6/cc save <file name> &7- &eCreate a new nbt file in the schematics folder.")
            add(" ")
            add("&6/key [player] &7- &eCheck the number of keys a player has.")
            add("&6/cc &7- &eOpens the menu.")
            add(" ")
            add("&7You can find a list of permissions @ &ehttps://github.com/badbones69/Crazy-Crates/wiki/Commands-and-Permissions")
        }
    }

    fun reload() {
        //reload(FileUtils().dataFolder!!.resolve("messages.yml"), this)
    }
}