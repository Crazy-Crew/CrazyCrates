package me.badbones69.crazycrates.multisupport;

import org.bukkit.Bukkit;

public class Support {

	public static boolean hasPlaceholderAPI() {
		return Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null;
	}

	public static boolean hasMVdWPlaceholderAPI() {
		return Bukkit.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null;
	}

}