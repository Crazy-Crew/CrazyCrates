package com.badbones69.crazycrates.support.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import org.bukkit.Location;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import net.Zrips.CMILib.Container.CMILocation;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMIHologramsSupport extends HologramManager {

    private final com.Zrips.CMI.Modules.Holograms.HologramManager hologramManager = CMI.getInstance().getHologramManager();

    private final Map<String, CMIHologram> holograms = new HashMap<>();

    @Override
    public void createHologram(Location location, Crate crate) {
        if (crate.getCrateType() == CrateType.menu) return;

        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) {
            removeHologram(location);

            return;
        }

        String loc = MiscUtils.location(location);

        if (this.holograms.containsKey(loc)) {
            CMIHologram hologram = this.holograms.get(loc);

            hologram.setShowRange(crateHologram.getRange());
            hologram.setLines(lines(crateHologram));

            this.holograms.put(loc, hologram);

            return;
        }

        CMIHologram hologram = new CMIHologram(name(), new CMILocation(location.clone().add(getVector(crate))));

        hologram.setNewDisplayMethod(true);
        hologram.setBillboard(CMIBillboard.CENTER);

        hologram.setShowRange(crateHologram.getRange());
        hologram.setLines(lines(crateHologram));

        this.hologramManager.addHologram(hologram);

        this.holograms.putIfAbsent(MiscUtils.location(location), hologram);
    }

    @Override
    public void removeHologram(Location location) {
        CMIHologram hologram = this.holograms.remove(MiscUtils.location(location));

        if (hologram != null) {
            hologram.remove();
        }
    }

    @Override
    public void removeAllHolograms(boolean isShutdown) {
        this.holograms.forEach((key, value) -> value.remove());
        this.holograms.clear();
    }

    @Override
    public boolean isEmpty() {
        return this.holograms.isEmpty();
    }
}