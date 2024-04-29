package com.badbones69.crazycrates.support.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import net.Zrips.CMILib.Container.CMILocation;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;
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

        //todo() make this work eventually, it's just not properly updating.
        /*String loc = MiscUtils.location(location);

        if (this.holograms.containsKey(loc)) {
            CMIHologram hologram = this.hologramManager.getByLoc(this.holograms.get(loc).getLocation());

            String color = crateHologram.getBackgroundColor();

            if (color.equalsIgnoreCase("transparent")) {
                hologram.setBackgroundAlpha(0);
            } else {
                hologram.setBackgroundColor(CMIChatColor.getByHex(color));
            }

            hologram.setShowRange(crateHologram.getRange());

            hologram.setLines(lines(crateHologram));

            location.getNearbyEntitiesByType(Player.class, crateHologram.getRange()).forEach(player -> this.hologramManager.handleHoloUpdates(player, hologram.getLocation()));

            this.holograms.put(loc, hologram);

            return;
        }*/

        CMIHologram hologram = new CMIHologram(name(), new CMILocation(location.clone().add(getVector(crate))));

        hologram.setNewDisplayMethod(true);
        hologram.setBillboard(CMIBillboard.CENTER);

        String color = crateHologram.getBackgroundColor();

        if (color.equalsIgnoreCase("transparent")) {
            hologram.setBackgroundAlpha(0);
        } else {
            hologram.setBackgroundColor(CMIChatColor.getClosest(color));
        }

        hologram.setShowRange(crateHologram.getRange());
        hologram.setLines(lines(crateHologram));

        this.hologramManager.addHologram(hologram);

        location.getNearbyEntitiesByType(Player.class, crateHologram.getRange()).forEach(player -> this.hologramManager.handleHoloUpdates(player, hologram.getLocation()));

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
        if (!isEmpty()) {
            this.holograms.forEach((key, value) -> value.remove());
            this.holograms.clear();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.holograms.isEmpty();
    }
}