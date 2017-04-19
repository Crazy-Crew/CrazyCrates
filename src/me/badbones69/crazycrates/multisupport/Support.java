package me.badbones69.crazycrates.multisupport;

import org.bukkit.Bukkit;

public class Support {
	
	public static boolean hasPlaceholderAPI(){
		if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
			return true;
		}
		return false;
	}
	
	public static boolean hasMVdWPlaceholderAPI(){
		if(Bukkit.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null){
			return true;
		}
		return false;
	}
	
}