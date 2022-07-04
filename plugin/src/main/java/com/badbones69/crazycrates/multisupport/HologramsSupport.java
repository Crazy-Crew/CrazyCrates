package com.badbones69.crazycrates.multisupport;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateHologram;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HologramsSupport implements HologramController {
    
    private static final HashMap<Block, Hologram> holograms = new HashMap<>();
    private static final HologramManager hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (crateHologram.isEnabled()) {
            double height = crateHologram.getHeight() - .5; // Doing this as Holograms seems to add .5 height when adding lines or something.
            Hologram hologram = new Hologram(new Random().nextInt() + "", block.getLocation().add(.5, height, .5));

            for (String line : crateHologram.getMessages()) {
                hologram.addLine(new TextLine(hologram, line));
            }

            hologramManager.addActiveHologram(hologram);
            hologram.spawn();
            holograms.put(block, hologram);
        }
    }
    
    public void removeHologram(Block block) {
        if (holograms.containsKey(block)) {
            Hologram hologram = holograms.get(block);
            hologram.despawn();
            hologramManager.deleteHologram(hologram);
            holograms.remove(block);
        }
    }
    
    public void removeAllHolograms() {
        for (Map.Entry<Block, Hologram> location : holograms.entrySet()) {
            Hologram hologram = location.getValue();
            hologramManager.deleteHologram(hologram);
        }

        holograms.clear();
    }
    
}