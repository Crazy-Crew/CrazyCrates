package me.BadBones69.CrazyCrates.MultiSupport;

import java.text.NumberFormat;

import org.bukkit.plugin.Plugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.Methods;

public class MVdWPlaceholderAPISupport {
	
	public static void registerPlaceholders(Plugin plugin){
		for(final String crate : Main.settings.getAllCratesNames()){
			PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate, new PlaceholderReplacer() {
				@Override
				public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
					return NumberFormat.getNumberInstance().format(Methods.getKeys(e.getPlayer(), crate));
				}
			});
		}
	}
	
}