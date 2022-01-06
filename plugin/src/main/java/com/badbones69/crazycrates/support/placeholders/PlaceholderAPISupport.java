package com.badbones69.crazycrates.support.placeholders;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.enums.CrateType;
import com.badbones69.crazycrates.api.objects.Crate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PlaceholderAPISupport extends PlaceholderExpansion {
    
    private CrazyManager cc = CrazyManager.getInstance();
    
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        if (player.isOnline()) {
            Player playerOnline = (Player) player;
            for (Crate crate : cc.getCrates()) {
                if (crate.getCrateType() != CrateType.MENU) {
                    if (identifier.equalsIgnoreCase(crate.getName())) {
                        return NumberFormat.getNumberInstance().format(cc.getVirtualKeys(playerOnline, crate));
                    } else if (identifier.equalsIgnoreCase(crate.getName() + "_physical")) {
                        return NumberFormat.getNumberInstance().format(cc.getPhysicalKeys(playerOnline, crate));
                    } else if (identifier.equalsIgnoreCase(crate.getName() + "_total")) {
                        return NumberFormat.getNumberInstance().format(cc.getTotalKeys(playerOnline, crate));
                    }
                }
            }
        }
        return "";
    }
    
    @Override
    public boolean persist() {
        return true;
    }
    
    @Override
    public String getIdentifier() {
        return "crazycrates";
    }
    
    @Override
    public String getAuthor() {
        return "BadBones69";
    }
    
    @Override
    public String getVersion() {
        return CrazyManager.getJavaPlugin().getDescription().getVersion();
    }
    
}