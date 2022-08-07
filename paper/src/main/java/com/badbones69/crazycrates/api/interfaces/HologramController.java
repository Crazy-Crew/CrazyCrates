package com.badbones69.crazycrates.api.interfaces;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.block.Block;

public interface HologramController {
    
    void createHologram(Block block, Crate crate, CrazyCrates plugin);
    
    void removeHologram(Block block);
    
    void removeAllHolograms();
    
}