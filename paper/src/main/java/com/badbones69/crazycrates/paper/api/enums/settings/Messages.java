package com.badbones69.crazycrates.paper.api.enums.settings;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.FileManager;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.*;
import java.util.Map.Entry;

public enum Messages {

    NO_TELEPORTING("No-Teleporting", "&cYou may not teleport away while opening a Crate."),
    NO_COMMANDS_WHILE_CRATE_OPENED("No-Commands-While-In-Crate", "&cYou are not allowed to use commands while opening Crates."),
    FEATURE_DISABLED("Feature-Disabled", "&cThis feature is disabled. We have no ETA on when this will function."),
    NO_KEY("No-Key", "&cYou must have a %key% &cin your hand to use that Crate."),
    NO_VIRTUAL_KEY("No-Virtual-Key", "&cYou need a key to open that Crate."),
    NOT_ON_BLOCK("Not-On-Block", "&cYou must be standing on a block to use this Crate."),
    ALREADY_OPENING_CRATE("Already-Opening-Crate", "&cYou are already opening a Crate."),
    QUICK_CRATE_IN_USE("Quick-Crate-In-Use", "&cThat Crate is already in use. Please wait for the Crate to open up."),
    WORLD_DISABLED("World-Disabled", "&cI am sorry but Crates are disabled in %world%."),
    RELOAD("Reload", "&3You have reloaded the Config and Data Files."),
    NOT_ONLINE("Not-Online", "&cThe player &6%player% &cis not online at this time."),
    NO_PERMISSION("No-Permission", "&cYou do not have permission to use that command!"),
    CRATE_ALREADY_OPENED("Crate-Already-Opened", "&cYou are already opening a Crate."),
    CANT_BE_A_VIRTUAL_CRATE("Cant-Be-A-Virtual-Crate", "&cThat crate type cannot be used as a Virtual Crate."),
    INVENTORY_FULL("Inventory-Full", "&cYour inventory is full, Please make room before opening a Crate."),
    TO_CLOSE_TO_ANOTHER_PLAYER("To-Close-To-Another-Player", "&cYou are too close to a player that is opening their Crate."),
    NEEDS_MORE_ROOM("Needs-More-Room", "&cThere is not enough space to open that here."),
    OUT_OF_TIME("Out-Of-Time", "&cYou took 5 Minutes to open the crate so it closed."),
    MUST_BE_A_PLAYER("Must-Be-A-Player", "&cYou must be a player to use this command."),
    MUST_BE_A_CONSOLE_SENDER("Must-Be-A-Console-Sender", "&cYou must be using console to use this command."),
    MUST_BE_LOOKING_AT_A_BLOCK("Must-Be-Looking-At-A-Block", "&cYou must be looking at a block."),
    NOT_A_CRATE("Not-A-Crate", "&cThere is no crate called &6%crate%."),
    NOT_A_NUMBER("Not-A-Number", "&6%number% &cis not a number."),
    GIVEN_A_PLAYER_KEYS("Given-A-Player-Keys", "&7You have given &6%player% %amount% &7Keys."),
    CANNOT_GIVE_PLAYER_KEYS("Cannot-Give-Player-Keys", "&7You have been given &6%amount% %key% &7virtual keys because your inventory was full."),
    OBTAINING_KEYS("Obtaining-Keys", "&7You have been given &6%amount% %key% &7Keys."),
    GIVEN_EVERYONE_KEYS("Given-Everyone-Keys", "&7You have given everyone &6%amount% &7Keys."),
    GIVEN_OFFLINE_PLAYER_KEYS("Given-Offline-Player-Keys", "&7You have given &6%amount% &7key(s) to the offline player &6%player%."),
    TAKE_A_PLAYER_KEYS("Take-A-Player-Keys", "&7You have taken &6%amount% &7key(s) from &6%player%."),
    TAKE_OFFLINE_PLAYER_KEYS("Take-Offline-Player-Keys", "&7You have taken &6%amount% &7key(s) from the offline player &6%player%."),
    OPENED_A_CRATE("Opened-A-Crate", "&7You have opened the &6%crate% &7crate for &6%player%."),
    INTERNAL_ERROR("Internal-Error", "&cAn internal error has occurred. Please check the console for the full error."),
    CORRECT_USAGE("Correct-Usage", "&cThe correct usage for this command is &e%usage%"),
    UNKNOWN_COMMAND("Unknown-Command", "&cThis command is not known."),
    NO_ITEM_IN_HAND("No-Item-In-Hand", "&cYou need to have an item in your hand to add it to the crate."),
    ADDED_ITEM_WITH_EDITOR("Added-Item-With-Editor", "&7The item has been added to the %crate% Crate in prize #%prize%."),
    CREATED_PHYSICAL_CRATE("Created-Physical-Crate", Arrays.asList("%prefix%&7You have set that block to %crate%.", "&7To remove the crate shift break in creative to remove.")),
    REMOVED_PHYSICAL_CRATE("Removed-Physical-Crate", "%prefix% &7You have removed &6%id%."),
    PERSONAL_NO_VIRTUAL_KEYS("Keys.Personal.No-Virtual-Keys", "&8&l(&4&l!&8&l) &7You currently do not have any virtual keys."),
    PERSONAL_HEADER("Keys.Personal.Header", Collections.singletonList("&8&l(&6&l!&8&l) &7List of your current amount of keys.")),
    OTHER_PLAYER_NO_VIRTUAL_KEYS("Keys.Other-Player.No-Virtual-Keys", "&8&l(&4&l!&8&l) &7The player %player% does not have any keys.."),
    OTHER_PLAYER_HEADER("Keys.Other-Player.Header", Collections.singletonList("&8&l(&6&l!&8&l) &7List of %player%''s current amount of keys.")),
    PER_CRATE("Keys.Per-Crate", "%crate% &7&l>&8&l> &6%keys% keys"),
    PREVIEW_DISABLED("Preview-Disabled", "&cThe preview for that Crate is currently disabled."),
    NO_SCHEMATICS_FOUND("No-Schematics-Found", "&cNo schematic were found. Please make sure NBT files exist in the schematics folder if not delete the folder to regenerate."),
    NO_PRIZES_FOUND("No-Prizes-Found", "&cThis Crate contains no prizes that you can win."),
    SAME_PLAYER("Same-Player", "&cYou can't use this command on yourself."),
    PRIZE_ERROR("Prize-Error", "&cAn error has occurred while trying to give you the prize called &6%prize%&c in crate called &6%crate%&c. Please contact the server owner and show them this error."),
    NOT_ENOUGH_KEYS("Not-Enough-Keys", "&cYou do not have enough keys to transfer."),
    REQUIRED_KEYS("Required-Keys", "&7You need &c%key-amount% &7keys to open &c%crate%. &7You have &c%amount%."),
    TRANSFERRED_KEYS("Transferred-Keys", "&7You have transferred %amount% %crate% keys to %player%."),
    RECEIVED_TRANSFERRED_KEYS("Received-Transferred-Keys", "&7You have received %amount% %crate% keys from %player%."),
    NO_FILES_TO_CONVERT("Files-Converted.No-Files-To-Convert", "&cNo plugins that can be converted were found."),
    ERROR_CONVERTING_FILES("Files-Converted.Error-Converting-Files", "&cThere was an error while trying to convert files. Please check console for the error log."),
    HELP("Help",
    Arrays.asList(
    "&e&lCrazy Crates Player Help",
    "&6/key [player] &7- &eCheck the number of keys a player has.",
    "&6/cc &7- &eOpens the menu.")),
    ADMIN_HELP("Admin-Help",
    Arrays.asList(
    "&c&lCrazy Crates Admin Help",
    "",
    "&6/cc additem <crate> <prize> &7- &eAdd items in-game to a prize in a crate.",
    "&6/cc preview <crate> [player] &7- &eOpens the preview of a crate for a player.",
    "&6/cc list &7- &eLists all crates.",
    "&6/cc open <crate> [player] &7- &eTries to open a crate for a player if they have a key.",
    "&6/cc forceopen <crate> [player] &7- &eOpens a crate for a player for free.",
    "&6/cc tp <location> &7- &eTeleport to a Crate.",
    "&6/cc give <physical/virtual> <crate> [amount] [player] &7- &eAllows you to take keys from a player.",
    "&6/cc set <crate> &7- &eSet the block you are looking at as a crate.",
    "&6/cc set Menu &7- &eSet the block you are looking at to open the /cc menu.",
    "&6/cc reload &7- &eReloads the config/data files.",
    "&6/cc set1/set2 &7- &eSets position &c#1 &eor &c#2 for when making a new schematic for QuadCrates.",
    "&6/cc save <file name> &7- &eCreate a new nbt file in the schematics folder.",
    " ",
    "&6/key [player] &7- &eCheck the number of keys a player has.",
    "&6/cc &7- &eOpens the menu.",
    " ",
    "&7You can find a list of permissions @ &ehttps://github.com/Crazy-Crew/CrazyCrates/wiki/Commands-and-Permissions"
    ));

    private final String path;
    private String defaultMessage;
    private List<String> defaultListMessage;

    Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    Messages(String path, List<String> defaultListMessage) {
        this.path = path;
        this.defaultListMessage = defaultListMessage;
    }

    /**
     * A method that will "convert" a StringList meaning it'll loop
     * through the StringList then color the individual line and then
     * returning the "converted" StringBuilder.
     *
     * @param list to convert
     * @return the converted StringList
     */
    public static String convertList(List<String> list) {
        StringBuilder message = new StringBuilder();

        for (String line : list) {
            message.append(Methods.color(line)).append("\n");
        }

        return message.toString();
    }

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    /**
     * Adds any missing messages to the Messages.yml file based on values in the Messages enum.
     */
    public static void addMissingMessages() {
        FileConfiguration messages = FileManager.Files.MESSAGES.getFile();
        boolean saveFile = false;

        for (Messages message : values()) {
            if (!messages.contains("Messages." + message.getPath())) {
                saveFile = true;
                if (message.getDefaultMessage() != null) {
                    messages.set("Messages." + message.getPath(), message.getDefaultMessage());
                } else {
                    messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
                }
            }
        }

        String tooManyArgs = messages.getString("Messages.Too-Many-Args");
        String tooFewArgs = messages.getString("Messages.Not-Enough-Args");

        if (tooManyArgs != null) {
            plugin.getLogger().warning("Found outdated config entry: " + tooManyArgs);
            plugin.getLogger().warning("Removing now, Please use `Correct-Usage` from now on." );

            messages.set("Messages.Too-Many-Args", null);
            FileManager.Files.MESSAGES.saveFile();
        }

        if (tooFewArgs != null) {
            plugin.getLogger().warning("Found outdated config entry: " + tooFewArgs);
            plugin.getLogger().warning("Removing now, Please use `Correct-Usage` from now on." );

            messages.set("Messages.Not-Enough-Args", null);
            FileManager.Files.MESSAGES.saveFile();
        }

        if (saveFile) FileManager.Files.MESSAGES.saveFile();
    }

    /**
     * A method which takes a placeholder, Creates a hashmap
     * then populates the hashmap & returns a String.
     *
     * @param placeholder to replace
     * @param replacement the replacement for the placeholder
     * @param message to be edited
     * @return the edited message
     */
    public static String replacePlaceholders(String placeholder, String replacement, String message) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return replacePlaceholders(placeholders, message);
    }

    /**
     * A method which loops through a map of placeholders to
     * replace the placeholders in the String with the key/value
     *
     * @param placeholders the map of the placeholders
     * @param message to be edited
     * @return the edited message
     */
    public static String replacePlaceholders(Map<String, String> placeholders, String message) {
        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue())
            .replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }

        return message;
    }

    /**
     * A method which takes a placeholder, Creates a hashmap
     * then populates the hashmap & returns a String.
     *
     * @param placeholder to replace
     * @param replacement the replacement for the placeholder
     * @param messageList the StringList to edit
     * @return the edited message
     */
    public static List<String> replacePlaceholders(String placeholder, String replacement, List<String> messageList) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return replacePlaceholders(placeholders, messageList);
    }

    /**
     * A method which loops through a map of placeholders to
     * replace the placeholders in the StringList loop.
     *
     * @param placeholders the map of the placeholders
     * @param messageList the StringList to be edited
     * @return the edited StringList
     */
    public static List<String> replacePlaceholders(Map<String, String> placeholders, List<String> messageList) {
        List<String> newMessageList = new ArrayList<>();

        for (String message : messageList) {
            for (Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue())
                .replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        return newMessageList;
    }

    /**
     * Get the message with a prefix.
     *
     * @return the completed message
     */
    public String getMessage() {
        return getMessage(true);
    }

    /**
     * Get the message with placeholders.
     *
     * @param placeholder the placeholder
     * @param replacement the replacement
     * @return the completed message with prefix
     */
    public String getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, true);
    }

    /**
     * Get the message with placeholders but no replacement.
     * This will be automatically handled at a later point.
     *
     * @param placeholders the placeholders map
     * @return the completed message with placeholders and prefix
     */
    public String getMessage(Map<String, String> placeholders) {
        return getMessage(placeholders, true);
    }

    /**
     * Get the message without a prefix.
     *
     * @return the completed message
     */
    public String getMessageNoPrefix() {
        return getMessage(false);
    }

    /**
     * Get the message with placeholders but no prefix.
     *
     * @param placeholder the placeholder
     * @param replacement the replacement
     * @return the completed message with placeholders
     */
    public String getMessageNoPrefix(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);
        return getMessage(placeholders, false);
    }

    /**
     * Get the message with placeholders but no prefix.
     *
     * @param placeholders the placeholder map
     * @return the completed message with placeholders
     */
    public String getMessageNoPrefix(Map<String, String> placeholders) {
        return getMessage(placeholders, false);
    }

    /**
     * Get the message with prefix but empty hashmap.
     *
     * @param prefix whether a prefix is included
     * @return the completed message with empty hashmap and prefix
     */
    private String getMessage(boolean prefix) {
        return getMessage(new HashMap<>(), prefix);
    }

    /**
     * Checks if the message exists and if the message is a list
     * Replaces all placeholders & checks if a prefix is to be appended or not.
     * Defaults to the defaultMessage if it does not exist.
     *
     * @param placeholders the placeholders map
     * @param prefix whether a prefix is included
     * @return the completed message
     */
    private String getMessage(Map<String, String> placeholders, boolean prefix) {
        String message;
        boolean isList = isList();
        boolean exists = exists();

        if (isList) {
            if (exists) {
                message = Methods.color(convertList(FileManager.Files.MESSAGES.getFile().getStringList("Messages." + path)));
            } else {
                message = Methods.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists) {
                message = Methods.color(FileManager.Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                message = Methods.color(getDefaultMessage());
            }
        }

        for (Entry<String, String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
        }

        if (isList) { // Don't want to add a prefix to a list of messages.
            return Methods.color(message);
        } else { // If the message isn't a list.
            if (prefix) { // If the message needs a prefix.
                return Methods.getPrefix(message);
            } else { // If the message doesn't need a prefix.
                return Methods.color(message);
            }
        }
    }

    /**
     * Checks if the file contains the String.
     *
     * @return true if found otherwise false
     */
    private boolean exists() {
        return FileManager.Files.MESSAGES.getFile().contains("Messages." + path);
    }

    /**
     * Checks if the file contains the StringList.
     *
     * @return true if found otherwise false
     */
    private boolean isList() {
        if (FileManager.Files.MESSAGES.getFile().contains("Messages." + path)) {
            return !FileManager.Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
        } else {
            return defaultMessage == null;
        }
    }

    /**
     * Get the string path.
     *
     * @return the path
     */
    private String getPath() {
        return path;
    }

    /**
     * Get the default message.
     *
     * @return the default message
     */
    private String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * Get the default list message.
     *
     * @return the default list message.
     */
    private List<String> getDefaultListMessage() {
        return defaultListMessage;
    }
}