package me.badbones69.crazycrates.api;

import java.util.HashMap;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;

public enum Messages {
	
	NO_TELEPORTING_MESSAGE("No-Teleporting-Msg"),
	NO_COMMANDS_WHILE_CRATE_OPENED("NoCMDsWhileCrateOpened"),
	NO_KEY_MESSAGE("NoKeyMsg"),
	NO_VIRTUAL_KEY_MESSAGE("NoVirtualKeyMsg"),
	ALREADY_OPENING_CRATE_MESSAGE("AlreadyOpeningCrateMsg"),
	QUICK_CRATE_IN_USE("QuickCrateInUse"),
	WORLD_DISABLED_MESSAGE("WorldDisabledMsg"),
	RELOAD("Reload"),
	NOT_ONLINE("Not-Online"),
	NO_PERMISSION("No-Permission"),
	CRATE_ALREADY_OPENED("Crate-Already-Opened"),
	CANT_BE_A_VIRTUAL_CRATE("Cant-Be-Virtual-Crate"),
	INVENTORY_FULL("Inventory-Full"),
	TO_CLOSE_TO_ANOTHER_PLAYER("QuadCrate.ToCloseToAnotherPlayer"),
	NEEDS_MORE_ROOM("QuadCrate.NeedsMoreRoom"),
	OUT_OF_TIME("QuadCrate.Out-Of-Time");
	
	private String path;
	
	private Messages(String path){
		this.path = path;
	}
	
	public String getMessage(){
		return Methods.getPrefix(Main.settings.getConfig().getString("Settings." + path));
	}
	
	public String getMessage(HashMap<String, String> placeholders){
		String msg = Methods.getPrefix(Main.settings.getConfig().getString("Settings." + path));
		for(String ph : placeholders.keySet()){
			if(msg.contains(ph)){
				msg = msg.replaceAll(ph, placeholders.get(ph));
			}
		}
		return msg;
	}
	
}