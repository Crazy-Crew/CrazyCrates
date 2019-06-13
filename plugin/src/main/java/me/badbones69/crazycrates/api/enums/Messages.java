package me.badbones69.crazycrates.api.enums;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.controllers.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Messages {
	
	NO_TELEPORTING("No-Teleporting", "&cYou may not teleport away while opening a crate."),
	NO_COMMANDS_WHILE_CRATE_OPENED("No-Commands-While-In-Crate", "&cYou are not allowed to use commands while opening Crates."),
	NO_KEY("No-Key", "&cYou must have a %key% &cin your hand to use that Crate."),
	NO_VIRTUAL_KEY("No-Virtual-Key", "&cYou need a key to open that Crate."),
	NOT_ON_BLOCK("Not-On-Block", "&cYou must be standing on a block to use this crate."),
	ALREADY_OPENING_CRATE("Already-Opening-Crate", "&cYou are already opening a crate."),
	QUICK_CRATE_IN_USE("Quick-Crate-In-Use", "&cThat crate is already in use. Please wait for the crate to open up."),
	WORLD_DISABLED("World-Disabled", "&cI am sorry but crates are disabled in %world%."),
	RELOAD("Reload", "&3You have just reloaded the Config and Data Files."),
	NOT_ONLINE("Not-Online", "&cThe player &6%player% &cis not online at this time."),
	NO_PERMISSION("No-Permission", "&cYou do not have permission to use that command!"),
	CRATE_ALREADY_OPENED("Crate-Already-Opened", "&cYou are already opening a crate."),
	CANT_BE_A_VIRTUAL_CRATE("Cant-Be-A-Virtual-Crate", "&cThat crate type cannot be used as a virtual crate."),
	INVENTORY_FULL("Inventory-Full", "&cYour inventory is full, please make room before opening a crate."),
	TO_CLOSE_TO_ANOTHER_PLAYER("To-Close-To-Another-Player", "&cYou are too close to a player that is opening their Crate."),
	NEEDS_MORE_ROOM("Needs-More-Room", "&cThere is not enough space to open that here."),
	OUT_OF_TIME("Out-Of-Time", "&cYou took 5 Minutes to open the crate so it closed."),
	MUST_BE_A_PLAYER("Must-Be-A-Player", "&cYou must be a player to use this command."),
	MUST_BE_LOOKING_AT_A_BLOCK("Must-Be-Looking-At-A-Block", "&cYou must be looking at a block."),
	NOT_A_CRATE("Not-A-Crate", "&cThere is no crate called &6%crate%&c."),
	NOT_A_NUMBER("Not-A-Number", "&6%number%&c is not a number."),
	GIVEN_A_PLAYER_KEYS("Given-A-Player-Keys", "&7You have given &6%player% %amount% &7Keys."),
	GIVEN_EVERYONE_KEYS("Given-Everyone-Keys", "&7You have given everyone &6%amount% &7Keys."),
	GIVEN_OFFLINE_PLAYER_KEYS("Given-Offline-Player-Keys", "&7You have given &6%amount% &7key(s) to the offline player &6%player%&7."),
	TAKE_A_PLAYER_KEYS("Take-A-Player-Keys", "&7You have taken &6%amount% &7key(s) from &6%player%&7."),
	TAKE_OFFLINE_PLAYER_KEYS("Take-Offline-Player-Keys", "&7You have taken &6%amount% &7key(s) from the offline player &6%player%&7."),
	OPENED_A_CRATE("Opened-A-Crate", "&7You have just opened the &6%crate% &7crate for &6%player%&7."),
	INTERNAL_ERROR("Internal-Error", "&cAn internal error has occurred. Please check the console for the full error."),
	NO_ITEM_IN_HAND("No-Item-In-Hand", "&cYou need to have an item in your hand to add it to the crate."),
	ADDED_ITEM_WITH_EDITOR("Added-Item-With-Editor", "&7The item has been added to the %crate% crate in prize #%prize%."),
	CREATED_PHYSICAL_CRATE("Created-Physical-Crate", Arrays.asList("%prefix%&7You have just set that block to %crate%.", "&7To remove the crate shift break in creative to remove.")),
	PERSONAL_NO_VIRTUAL_KEYS("Keys.Personal.No-Virtual-Keys", "&8&l(&4&l!&8&l) &7You currently do not have any virtual keys."),
	PERSONAL_HEADER("Keys.Personal.Header", Arrays.asList("&8&l(&6&l!&8&l) &7List of your current amount of keys.")),
	OTHER_PLAYER_NO_VIRTUAL_KEYS("Keys.Other-Player.No-Virtual-Keys", "&8&l(&4&l!&8&l) &7The player %player% does not have any keys.."),
	OTHER_PLAYER_HEADER("Keys.Other-Player.Header", Arrays.asList("&8&l(&6&l!&8&l) &7List of %player%''s current amount of keys.")),
	PER_CRATE("Keys.Per-Crate", "%crate% &7&l>&8&l> &6%keys% keys"),
	QUAD_CRATE_DISABLED("Quad-Crate-Disabled", "&cQuad crate type is currently disabled in this build of Crazy Crates due to 1.13+ changing code."),
	PREVIEW_DISABLED("Preview-Disabled", "&cThe preview for that crate is currently disabled."),
	HELP("Help",
	Arrays.asList("&3&lCrazy Crates Help Menu",
	"&6/key [player] &7- Check the amount of keys a player has.",
	"&6/cc &7- Opens the GUI.",
	"&6/cc additem <crate> <prize> &7- Add items in-game to a prize in a crate.",
	"&6/cc admin &7- Opens the Admin Keys GUI.",
	"&6/cc list &7- Lists all the Crates.",
	"&6/cc open <Crate> [Player] &7- Opens a crate for a player.",
	"&6/cc tp <Location> &7- Teleport to a Crate.",
	"&6/cc give <Physical/Virtual> <Crate> [Amount] [Player] &7- Give a player keys for a Chest.",
	"&6/cc giveall <Physical/Virtual> <Crate> [Amount] &7- Gives all online players keys for a Chest.",
	"&6/cc take <Physical/Virtual> <Crate> [Amount] [Player] &7- Allows you to take keys from a player.",
	"&6/cc set <Crate> &7- Set a block you are looking at as a crate.",
	"&6/cc set Menu &7- Set the block you are looking at to open the /cc GUI.",
	"&6/cc reload &7- Reloads the Config and Data Files.",
	"&6/cc set1/set2 &7- Set position #1 or #2 for when making a new schematic for quadcrates. &c1.13+ only",
	"&6/cc save <schematic file name> &7- Save the new schematic file to the schematics folder. &c1.13+ only"));
	
	private String path;
	private String defaultMessage;
	private List<String> defaultListMessage;
	
	private Messages(String path, String defaultMessage) {
		this.path = path;
		this.defaultMessage = defaultMessage;
	}
	
	private Messages(String path, List<String> defaultListMessage) {
		this.path = path;
		this.defaultListMessage = defaultListMessage;
	}
	
	public String getMessage() {
		if(isList()) {
			if(exists()) {
				return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
			}else {
				return Methods.color(convertList(getDefaultListMessage()));
			}
		}else {
			if(exists()) {
				return Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				return Methods.getPrefix(getDefaultMessage());
			}
		}
	}
	
	public String getMessage(HashMap<String, String> placeholders) {
		String message;
		if(isList()) {
			if(exists()) {
				message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
			}else {
				message = Methods.color(convertList(getDefaultListMessage(), placeholders));
			}
		}else {
			if(exists()) {
				message = Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				message = Methods.getPrefix(getDefaultMessage());
			}
			for(String ph : placeholders.keySet()) {
				if(message.contains(ph)) {
					message = message.replaceAll(ph, placeholders.get(ph));
				}
			}
		}
		return message;
	}
	
	public String getMessageNoPrefix() {
		if(isList()) {
			if(exists()) {
				return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
			}else {
				return Methods.color(convertList(getDefaultListMessage()));
			}
		}else {
			if(exists()) {
				return Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				return Methods.color(getDefaultMessage());
			}
		}
	}
	
	public String getMessageNoPrefix(HashMap<String, String> placeholders) {
		String message;
		if(isList()) {
			if(exists()) {
				message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
			}else {
				message = Methods.color(convertList(getDefaultListMessage(), placeholders));
			}
		}else {
			if(exists()) {
				message = Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
			}else {
				message = Methods.color(getDefaultMessage());
			}
			for(String ph : placeholders.keySet()) {
				if(message.contains(ph)) {
					message = message.replaceAll(ph, placeholders.get(ph));
				}
			}
		}
		return message;
	}
	
	public static String convertList(List<String> list) {
		String message = "";
		for(String m : list) {
			message += Methods.color(m) + "\n";
		}
		return message;
	}
	
	public static String convertList(List<String> list, HashMap<String, String> placeholders) {
		String message = "";
		for(String m : list) {
			message += Methods.color(m) + "\n";
		}
		for(String ph : placeholders.keySet()) {
			message = Methods.color(message.replaceAll(ph, placeholders.get(ph)));
		}
		return message;
	}
	
	public static void addMissingMessages() {
		FileConfiguration messages = Files.MESSAGES.getFile();
		boolean saveFile = false;
		for(Messages message : values()) {
			if(!messages.contains("Messages." + message.getPath())) {
				saveFile = true;
				if(message.getDefaultMessage() != null) {
					messages.set("Messages." + message.getPath(), message.getDefaultMessage());
				}else {
					messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
				}
			}
		}
		if(saveFile) {
			Files.MESSAGES.saveFile();
		}
	}
	
	private Boolean exists() {
		return Files.MESSAGES.getFile().contains("Messages." + path);
	}
	
	private Boolean isList() {
		if(Files.MESSAGES.getFile().contains("Messages." + path)) {
			return !Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
		}else {
			return defaultMessage == null;
		}
	}
	
	private String getPath() {
		return path;
	}
	
	private String getDefaultMessage() {
		return defaultMessage;
	}
	
	private List<String> getDefaultListMessage() {
		return defaultListMessage;
	}
	
}