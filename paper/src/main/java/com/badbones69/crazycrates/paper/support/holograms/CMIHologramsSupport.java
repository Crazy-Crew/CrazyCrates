package com.badbones69.crazycrates.paper.support.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import net.Zrips.CMILib.Container.CMILocation;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.UUID;

public class CMIHologramsSupport implements HologramController {

    private final HashMap<Block, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Crate crate) {
        double height = crate.getHologram().getHeight();

        CMILocation location = new CMILocation(block.getLocation().add(0.5, height, 0.5));

        CMIHologram hologram = new CMIHologram("CrazyCrates-" + UUID.randomUUID(), location);

        hologram.setLines(crate.getHologram().getMessages());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        hologram.update();

        holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        CMIHologram hologram = holograms.get(block);

        holograms.remove(block);

        hologram.remove();
    }

    @Override
    public void removeAllHolograms() {
        holograms.forEach((key, value) -> value.remove());
        holograms.clear();
    }
}