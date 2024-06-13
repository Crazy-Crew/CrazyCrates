package com.badbones69.crazycrates.support.holograms.types;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Display.CMIBillboard;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.badbones69.crazycrates.api.crates.CrateHologram;
import net.Zrips.CMILib.Colors.CMIChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import net.Zrips.CMILib.Container.CMILocation;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class CMIHologramsSupport extends HologramManager {

    private final com.Zrips.CMI.Modules.Holograms.HologramManager hologramManager = CMI.getInstance().getHologramManager();

    @Override
    public void createHologram(Location location, Crate crate, String id) {
        if (crate.getCrateType() == CrateType.menu) return;

        final CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) {
            removeHologram(id);

            return;
        }

        final CMIHologram hologram = new CMIHologram("crazycrates-" + id, new CMILocation(location.clone().add(getVector(crate))));

        hologram.setNewDisplayMethod(true);
        hologram.setBillboard(CMIBillboard.CENTER);

        final String color = crateHologram.getBackgroundColor();

        if (color.equalsIgnoreCase("transparent")) {
            hologram.setBackgroundAlpha(0);
        } else {
            hologram.setBackgroundColor(CMIChatColor.getClosest(color));
        }

        hologram.setShowRange(crateHologram.getRange());
        hologram.setLines(lines(crateHologram));

        this.hologramManager.addHologram(hologram);

        location.getNearbyEntitiesByType(Player.class, crateHologram.getRange()).forEach(player -> this.hologramManager.handleHoloUpdates(player, hologram.getLocation()));
    }

    @Override
    public void removeHologram(String id) {
        final CMIHologram hologram = this.hologramManager.getByName(id);

        if (hologram != null) {
            hologram.remove();

            this.hologramManager.removeHolo(hologram);
        }
    }

    @Override
    public void purge(boolean isShutdown) {
        this.hologramManager.getHolograms().forEach((id, hologram) -> {
            if (!isShutdown) {
                this.plugin.getServer().getOnlinePlayers().forEach(player -> hologram.hide(player.getUniqueId()));
            }

            hologram.remove();

            this.hologramManager.removeHolo(hologram);
        });
    }
}