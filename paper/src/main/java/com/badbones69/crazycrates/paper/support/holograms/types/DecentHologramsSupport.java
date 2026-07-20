package com.badbones69.crazycrates.paper.support.holograms.types;

import com.badbones69.crazycrates.paper.api.objects.crates.CrateHologram;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.utils.color.IridiumColorAPI;
import org.bukkit.Location;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import com.badbones69.crazycrates.paper.support.holograms.HologramManager;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;
import java.util.Map;

public class DecentHologramsSupport extends HologramManager {

    private final Map<String, Hologram> holograms = new HashMap<>();

    @Override
    public void createHologram(@NotNull final Location location, @NotNull final Crate crate, @NotNull final String id) {
        if (crate.getCrateType() == CrateType.menu) return;

        final CrateHologram crateHologram = crate.getHologram();

        final String identifier = name(id);

        if (!crateHologram.isEnabled()) {
            removeHologram(identifier);

            return;
        }

        // We don't want to create a new one if one already exists.
        if (exists(identifier)) return;

        final Hologram hologram = DHAPI.createHologram(identifier, location.clone().add(getVector(crate)));

        crateHologram.getMessages().forEach(line -> DHAPI.addHologramLine(hologram, IridiumColorAPI.process(line)));

        hologram.setDisplayRange(crateHologram.getRange());

        if (crateHologram.getUpdateInterval() != -1) {
            hologram.setUpdateInterval(crateHologram.getUpdateInterval());
        }

        this.holograms.putIfAbsent(identifier, hologram);
    }

    @Override
    public void removeHologram(@NotNull final String id) {
        DHAPI.removeHologram(id);
    }

    @Override
    public boolean exists(@NotNull final String id) {
        return DHAPI.getHologram(id) != null;
    }

    @Override
    public void purge(final boolean isShutdown) {
        this.holograms.forEach((key, value) -> {
            removeHologram(key);

            value.delete();
        });

        this.holograms.clear();
    }

    @Override
    public @NotNull final String getName() {
        return "DecentHolograms";
    }
}