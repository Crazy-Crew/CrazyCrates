package com.badbones69.crazycrates.support.holograms.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import eu.decentsoftware.holograms.api.DHAPI;
import org.bukkit.Location;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;
import java.util.Map;

public class DecentHologramsSupport extends HologramManager {

    private final Map<String, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Location location, Crate crate) {
        if (crate.getCrateType() == CrateType.menu) return;

        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) {
            removeHologram(location);

            return;
        }

        Hologram hologram = DHAPI.createHologram(name(), location.clone().add(getVector(crate)));

        DHAPI.addHologramPage(hologram, lines(crateHologram));

        crateHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, color(line)));

        hologram.setDisplayRange(crateHologram.getRange());

        this.holograms.putIfAbsent(MiscUtils.location(location), hologram);
    }

    @Override
    public void removeHologram(Location location) {
        Hologram hologram = this.holograms.remove(MiscUtils.location(location));

        if (hologram != null) {
            hologram.destroy();
        }
    }

    @Override
    public void removeAllHolograms(boolean isShutdown) {
        if (!isEmpty()) {
            this.holograms.forEach((key, value) -> value.destroy());
            this.holograms.clear();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.holograms.isEmpty();
    }
}