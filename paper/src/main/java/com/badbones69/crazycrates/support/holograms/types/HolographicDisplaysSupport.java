package com.badbones69.crazycrates.support.holograms.types;


import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.crates.CrateHologram;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.block.Block;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.HashMap;
import java.util.Map;

public class HolographicDisplaysSupport extends HologramManager {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(this.plugin);
    
    private final Map<Block, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Crate crate) {
        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        double height = crateHologram.getHeight();

        Hologram hologram = this.api.createHologram(block.getLocation().add(.5, height, .5));

        crateHologram.getMessages().forEach(line -> hologram.getLines().appendText(MsgUtils.color(line)));

        this.holograms.put(block, hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!this.holograms.containsKey(block)) return;

        Hologram hologram = this.holograms.get(block);

        this.holograms.remove(block);
        hologram.delete();
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> value.delete());
        this.holograms.clear();
    }
}