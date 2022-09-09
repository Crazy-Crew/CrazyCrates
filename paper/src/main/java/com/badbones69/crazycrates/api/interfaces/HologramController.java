package com.badbones69.crazycrates.api.interfaces;

import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import org.bukkit.block.Block;

public interface HologramController {

    /**
     * The hologram that is to be created.
     * @param block - The block where the hologram should build against.
     * @param crate - The crate that the hologram is for.
     */
    void createHologram(Block block, Crate crate);

    /**
     * The hologram that is to be deleted.
     * @param block - The block where the hologram is at.
     */
    void removeHologram(Block block);

    /**
     * Remove all active holograms.
     */
    void removeAllHolograms();
}