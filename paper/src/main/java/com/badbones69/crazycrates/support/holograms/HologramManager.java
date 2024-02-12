package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.block.Block;

public abstract class HologramManager {
    
    public abstract void createHologram(Block block, Crate crate);

    public abstract void removeHologram(Block block);

    public abstract void removeAllHolograms();
    
}