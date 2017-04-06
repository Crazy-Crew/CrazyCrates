package me.badbones69.crazycrates.multisupport;

import java.text.NumberFormat;

import org.bukkit.plugin.Plugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.Crate;

public class MVdWPlaceholderAPISupport {
	
	public static void registerPlaceholders(Plugin plugin){
		for(final Crate crate : Main.CC.getCrates()){
			PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate, new PlaceholderReplacer() {
				@Override
				public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
					return NumberFormat.getNumberInstance().format(Methods.getKeys(e.getPlayer(), crate));
				}
			});
		}
	}
	
}