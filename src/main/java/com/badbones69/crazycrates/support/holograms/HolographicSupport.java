package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.CrateHologram;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.block.Block;
import java.util.HashMap;
import static com.badbones69.crazycrates.support.utils.ConstantsKt.color;

public class HolographicSupport implements HologramController {
    
    private static final HashMap<Block, Hologram> holograms = new HashMap<>();

    private final CrazyManager crazyManager = CrazyManager.getInstance();
    
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();
        if (!crateHologram.isEnabled()) return;
        double height = crateHologram.getHeight();
        Hologram hologram = HologramsAPI.createHologram(crazyManager.getPlugin(), block.getLocation().add(.5, height, .5));
        crateHologram.getMessages().forEach(line -> hologram.appendTextLine(color(line)));
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