package me.badbones69.crazycrates.multisupport.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateHologram;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicSupport implements HologramController {

    private static final HashMap<Block, Hologram> holograms = new HashMap<>();

    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();
        if (!crateHologram.isEnabled()) return;
        double height = crateHologram.getHeight();
        Hologram hologram = HologramsAPI.createHologram(CrazyManager.getJavaPlugin(), block.getLocation().add(.5, height, .5));
        crateHologram.getMessages().forEach(line -> {
            hologram.appendTextLine(Methods.color(line));
        });
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