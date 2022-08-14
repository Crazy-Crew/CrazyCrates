package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.inject.Inject;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicSupport implements HologramController {
    
    private static final HashMap<Block, Hologram> holograms = new HashMap<>();

    @Inject private CrazyCrates plugin;
    @Inject private Methods methods;
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();
        Hologram hologram = HologramsAPI.createHologram(plugin, block.getLocation().add(.5, height, .5));
        crateHologram.getMessages().forEach(line -> hologram.appendTextLine(methods.color(line)));
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