package me.badbones69.crazycrates.multisupport;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;

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
					return NumberFormat.getNumberInstance().format(cc.getVirtualKeys(player, crate));
				}else if(placeHolder.equalsIgnoreCase(crate.getName() + "_physical")) {
					return NumberFormat.getNumberInstance().format(cc.getPhysicalKey(player, crate));
				}else if(placeHolder.equalsIgnoreCase(crate.getName() + "_total")) {
					return NumberFormat.getNumberInstance().format(cc.getTotalKeys(player, crate));
				}
			}
		}
		return null;
	}
	
}