package com.badbones69.crazycrates.api.interfaces;

import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import org.bukkit.block.Block;

public interface HologramController {
    
    void createHologram(Block block, Crate crate);
    
    void removeHologram(Block block);
    
    void removeAllHolograms();
    
}