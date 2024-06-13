package com.badbones69.crazycrates.support.holograms.types;

import com.badbones69.crazycrates.api.crates.CrateHologram;
import com.badbones69.crazycrates.api.objects.Crate;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import org.bukkit.Location;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class DecentHologramsSupport extends HologramManager {

    private final List<Hologram> holograms = new ArrayList<>();

    @Override
    public void createHologram(Location location, Crate crate, String id) {
        if (crate.getCrateType() == CrateType.menu) return;

        final CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) {
            removeHologram(id);

            return;
        }

        final Hologram hologram = DHAPI.createHologram("crazycrates-" + id, location.clone().add(getVector(crate)));

        crateHologram.getMessages().forEach(line -> {
            if (line != null) {
                String coloredLine = color(line);

                if (coloredLine != null) {
                    DHAPI.addHologramLine(hologram, coloredLine);
                }
            }
        });

        hologram.setDisplayRange(crateHologram.getRange());

        this.holograms.add(hologram);
    }

    @Override
    public void removeHologram(String id) {
        Hologram key = null;

        for (Hologram hologram : this.holograms) {
            if (hologram.getId().equals(id)) {
                key = hologram;

                break;
            }
        }

        if (key != null) {
            key.hideAll();

            this.holograms.remove(key);
        }

        DHAPI.removeHologram(id);
    }

    @Override
    public void purge(boolean isShutdown) {
        this.holograms.forEach(hologram -> {
            if (!isShutdown) {
                hologram.hideAll();
            }

            hologram.delete();
        });
    }
}