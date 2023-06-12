package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.interfaces.HologramController;
import com.badbones69.crazycrates.api.objects.Crate;
import de.oliver.fancyholograms.Hologram;
import de.oliver.fancyholograms.utils.HologramSpigotAdapter;
import net.minecraft.world.entity.Display;
import org.bukkit.Location;
import org.bukkit.block.Block;
import java.util.HashMap;
import java.util.UUID;

public class FancyHologramSupport implements HologramController {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final HashMap<Location, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Block block, Crate crate) {
        if (!crate.getHologram().isEnabled()) return;

        double height = crate.getHologram().getHeight();

        Hologram hologram = new Hologram("CrazyCrates-" + UUID.randomUUID(), block.getLocation().add(0.5, height, 0.5), crate.getHologram().getMessages(), Display.BillboardConstraints.CENTER, 1f, null, 0, 1, -1, null);

        hologram.create();

        HologramSpigotAdapter spigotAdapter = HologramSpigotAdapter.fromHologram(hologram);

        plugin.getServer().getOnlinePlayers().forEach(spigotAdapter::spawn);

        holograms.put(block.getLocation(), hologram);
    }

    @Override
    public void removeHologram(Block block) {
        if (!holograms.containsKey(block.getLocation())) return;

        Hologram hologram = holograms.get(block.getLocation());

        HologramSpigotAdapter spigotAdapter = HologramSpigotAdapter.fromHologram(hologram);

        plugin.getServer().getOnlinePlayers().forEach(spigotAdapter::remove);

        hologram.delete();

        holograms.remove(block.getLocation());
    }

    @Override
    public void removeAllHolograms() {
        holograms.forEach((key, value) -> {
            HologramSpigotAdapter spigotAdapter = HologramSpigotAdapter.fromHologram(value);

            plugin.getServer().getOnlinePlayers().forEach(spigotAdapter::remove);

            value.delete();
        });

        holograms.clear();
    }
}