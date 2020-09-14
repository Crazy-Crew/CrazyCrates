package me.badbones69.crazycrates.multisupport;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateHologram;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class HolographicSupport implements HologramController {

    private static final CrazyCrates cc = CrazyCrates.getInstance();
    private static final HashMap<Block, Hologram> holograms = new HashMap<>();

    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();
        if (crateHologram.isEnabled()) {
            double hight = crateHologram.getHeight();
            Hologram hologram = HologramsAPI.createHologram(cc.getPlugin(), block.getLocation().add(.5, hight, .5));
            for (String line : crateHologram.getMessages()) {
                hologram.appendTextLine(Methods.color(line));
            }
            holograms.put(block, hologram);
        }
    }
    
    public void removeHologram(Block block) {
        if (holograms.containsKey(block)) {
            Hologram hologram = holograms.get(block);
            holograms.remove(block);
            hologram.delete();
        }
    }
    
    public void removeAllHolograms() {
        for (Map.Entry<Block, Hologram> block : holograms.entrySet()) {
            block.getValue().delete();
        }
        holograms.clear();
    }
    
}