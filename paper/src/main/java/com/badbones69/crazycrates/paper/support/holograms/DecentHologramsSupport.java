package com.badbones69.crazycrates.paper.support.holograms;

import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateHologram;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.UUID;

public class DecentHologramsSupport implements HologramController {

    private final HashMap<Block, Hologram> holograms = new HashMap<>();
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();

        Hologram hologram = DHAPI.createHologram("CrazyCrates-" + UUID.randomUUID(), block.getLocation().add(.5, height, .5));

        crateHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, Methods.color(line)));

        holograms.put(block, hologram);
    }
    
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        Hologram hologram = holograms.get(block);

        holograms.remove(block);
        hologram.delete();
    }

    public void removeAllHolograms() {
        holograms.forEach((key, value) -> value.delete());
        holograms.clear();
    }
}