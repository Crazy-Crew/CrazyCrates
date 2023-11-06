package us.crazycrew.crazycrates.paper.api.interfaces;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.block.Block;

public abstract class HologramHandler {
    
    public abstract void createHologram(Block block, Crate crate);

    public abstract void removeHologram(Block block);

    public abstract void removeAllHolograms();
    
}