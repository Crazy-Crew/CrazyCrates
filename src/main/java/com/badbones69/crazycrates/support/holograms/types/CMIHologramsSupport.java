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
    public void createHologram(final Location location, final Crate crate, final String id) {
        if (crate.getCrateType() == CrateType.menu) return;

        final CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) {
            removeHologram(id);

            return;
        }

        // We don't want to create a new one if one already exists.
        if (exists(id)) return;

        final CMIHologram hologram = new CMIHologram(name(id), new CMILocation(location.clone().add(getVector(crate))));

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

        if (crateHologram.getUpdateInterval() != -1) {
            hologram.setUpdateIntervalSec(crateHologram.getUpdateInterval());
        }

        this.hologramManager.addHologram(hologram);

        location.getNearbyEntitiesByType(Player.class, crateHologram.getRange()).forEach(player -> this.hologramManager.handleHoloUpdates(player, hologram.getLocation()));
    }

    @Override
    public void removeHologram(final String id) {
        final CMIHologram hologram = this.hologramManager.getByName(name(id));

        if (hologram != null) {
            hologram.remove();
        }
    }

    @Override
    public boolean exists(final String id) {
        return this.hologramManager.getByName(name(id)) != null;
    }

    @Override
    public void purge(final boolean isShutdown) {
        final String name = this.plugin.getName().toLowerCase();

        final List<String> holograms = new ArrayList<>() {{
            hologramManager.getHolograms().forEach((id, hologram) -> {
                if (id.startsWith(name + "-")) {
                    add(id.replace(name + "-", ""));
                }
            });
        }};

        holograms.forEach(this::removeHologram);
    }

    @Override
    public final String getName() {
        return "CMI";
    }
}