package me.badbones69.crazycrates.multisupport;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.plugin.Plugin;

import java.text.NumberFormat;

public class MVdWPlaceholderAPISupport {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	
	public static void registerPlaceholders(Plugin plugin) {
		for(final Crate crate : cc.getCrates()) {
			if(crate.getCrateType() != CrateType.MENU) {
				PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate, e -> NumberFormat.getNumberInstance().format(cc.getVirtualKeys(e.getPlayer(), crate)));
				PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate + "_physical", e -> NumberFormat.getNumberInstance().format(cc.getPhysicalKeys(e.getPlayer(), crate)));
				PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate + "_total", e -> NumberFormat.getNumberInstance().format(cc.getTotalKeys(e.getPlayer(), crate)));
			}
		}
	}
	
}