package com.badbones69.crazycrates.support.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazycrates.common.crates.CrateHologram;
import net.Zrips.CMILib.Container.CMILocation;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.block.Block;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CMIHologramsSupport extends HologramManager {

    private final Map<Block, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();

        CMILocation location = new CMILocation(block.getLocation().add(0.5, height, 0.5));

        CMIHologram hologram = new CMIHologram("CrazyCrates-" + UUID.randomUUID(), location);

        hologram.setLines(crateHologram.getMessages());

        hologram.setShowRange(crateHologram.getRange());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        hologram.update();

        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        CMIHologram hologram = this.holograms.get(block);

        this.holograms.remove(block);

        hologram.remove();
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> value.remove());
        this.holograms.clear();
    }
}