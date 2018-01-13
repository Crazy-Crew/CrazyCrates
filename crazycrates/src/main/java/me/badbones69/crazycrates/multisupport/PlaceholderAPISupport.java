package me.badbones69.crazycrates.multisupport;

import java.text.NumberFormat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import me.clip.placeholderapi.external.EZPlaceholderHook;

public class PlaceholderAPISupport extends EZPlaceholderHook {
	
	private CrazyCrates cc = CrazyCrates.getInstance();
	
	public PlaceholderAPISupport(Plugin plugin) {
		super(plugin, "crazycrates");
	}
	
	@Override
	public String onPlaceholderRequest(Player player, String placeHolder) {
		for(Crate crate : cc.getCrates()) {
			if(crate.getCrateType() != CrateType.MENU) {
				if(placeHolder.equalsIgnoreCase(crate.getName())) {
					return NumberFormat.getNumberInstance().format(CrazyCrates.getInstance().getVirtualKeys(player, crate));
				}
			}
		}
		return null;
	}
	
}