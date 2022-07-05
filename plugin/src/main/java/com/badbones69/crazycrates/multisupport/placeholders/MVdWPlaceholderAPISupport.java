package com.badbones69.crazycrates.multisupport.placeholders;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.plugin.Plugin;
import java.text.NumberFormat;

public class MVdWPlaceholderAPISupport {
    
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    public static void registerPlaceholders(Plugin plugin) {
        for (final Crate crate : crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate.getName(), e -> NumberFormat.getNumberInstance().format(crazyManager.getVirtualKeys(e.getPlayer(), crate)));
                PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate.getName() + "_physical", e -> NumberFormat.getNumberInstance().format(crazyManager.getPhysicalKeys(e.getPlayer(), crate)));
                PlaceholderAPI.registerPlaceholder(plugin, "crazycrates_" + crate.getName() + "_total", e -> NumberFormat.getNumberInstance().format(crazyManager.getTotalKeys(e.getPlayer(), crate)));
            }
        }
    }
    
}