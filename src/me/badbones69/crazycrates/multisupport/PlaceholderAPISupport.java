package me.badbones69.crazycrates.multisupport;

import java.text.NumberFormat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.Crate;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderAPISupport extends EZPlaceholderHook{
	
	public PlaceholderAPISupport(Plugin plugin) {
		super(plugin, "crazycrates");
	}

	@Override
	public String onPlaceholderRequest(Player player, String placeHolder) {
		for(Crate crate : Main.CC.getCrates()){
			if(placeHolder.equalsIgnoreCase(crate.getName())){
				return NumberFormat.getNumberInstance().format(Methods.getKeys(player, crate));
			}
		}
		return null;
	}
	
}