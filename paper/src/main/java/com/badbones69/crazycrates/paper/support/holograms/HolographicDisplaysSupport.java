package com.badbones69.crazycrates.paper.support.holograms;

import com.badbones69.crazycrates.api.objects.CrateHologram;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.interfaces.HologramController;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.block.Block;
import java.util.HashMap;

public class HolographicDisplaysSupport implements HologramController {
    
    private final HashMap<Block, Hologram> holograms = new HashMap<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);

    @Override
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();

        Hologram hologram = api.createHologram(block.getLocation().add(.5, height, .5));

        crateHologram.getMessages().forEach(line -> hologram.getLines().appendText(Methods.color(line)));

        holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block)) return;

        Hologram hologram = holograms.get(block);

        holograms.remove(block);
        hologram.delete();
    }

    @Override
    public void removeAllHolograms() {
        holograms.forEach((key, value) -> value.delete());
        holograms.clear();
    }
}