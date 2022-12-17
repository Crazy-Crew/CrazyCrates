package com.badbones69.crazycrates.api.interfaces;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface HologramController {
    
    void createHologram(Block block, Crate crate);
    
    void removeHologram(Block block);

    void hideHologram(Player player, Block block);

    void showHologram(Player player, Block block);
    
    void removeAllHolograms();
    
}