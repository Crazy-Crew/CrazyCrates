package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.utilities.handlers.objects.crates.Crate;
import com.badbones69.crazycrates.common.configuration.objects.CrateHologram;
import com.badbones69.crazycrates.api.utilities.AdventureUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class DecentHologramsSupport implements HologramController {
    
    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Inject private AdventureUtils adventureUtils;
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();
        Hologram hologram = DHAPI.createHologram(ThreadLocalRandom.current().nextInt() + "", block.getLocation().add(.5, height, .5));
        crateHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, adventureUtils.parseMessage(line)));

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