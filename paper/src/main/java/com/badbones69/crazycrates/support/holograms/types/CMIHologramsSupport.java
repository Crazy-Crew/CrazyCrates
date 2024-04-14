package com.badbones69.crazycrates.support.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.bukkit.Location;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import net.Zrips.CMILib.Container.CMILocation;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;
import java.util.Map;

public class CMIHologramsSupport extends HologramManager {

    private final Map<String, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Location location, Crate crate) {
        if (crate.getCrateType() == CrateType.menu) return;

        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        CMIHologram hologram = new CMIHologram(name(), new CMILocation(location.clone().add(getVector(crate))));

        hologram.setLines(crateHologram.getMessages());
        hologram.setShowRange(crateHologram.getRange());

        CMI.getInstance().getHologramManager().addHologram(hologram);

        hologram.update();

        this.holograms.put(MiscUtils.location(location), hologram);
    }

    @Override
    public void removeHologram(Location location) {
        CMIHologram hologram = this.holograms.remove(MiscUtils.location(location));

        if (hologram != null) {
            hologram.remove();
        }
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> value.remove());
        this.holograms.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.holograms.isEmpty();
    }
}