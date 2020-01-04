package me.badbones69.crazycrates.api.interfaces;

import me.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.block.Block;

public interface HologramController {
    
    void createHologram(Block location, Crate crate);
    
    void removeHologram(Block location);
    
    void removeAllHolograms();
    
}