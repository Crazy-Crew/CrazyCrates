package me.BadBones69.CrazyCrates.MultiSupport;

import java.text.NumberFormat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.Methods;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderAPISupport extends EZPlaceholderHook{
	
	public PlaceholderAPISupport(Plugin plugin) {
		super(plugin, "crazycrates");
	}

	@Override
	public String onPlaceholderRequest(Player player, String placeHolder) {
		for(String crate : Main.settings.getAllCratesNames()){
			if(placeHolder.equalsIgnoreCase(crate)){
				return NumberFormat.getNumberInstance().format(Methods.getKeys(player, crate));
			}
		}
		return null;
	}
	
}