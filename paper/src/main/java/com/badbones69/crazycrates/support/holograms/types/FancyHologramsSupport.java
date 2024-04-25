package com.badbones69.crazycrates.support.holograms.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FancyHologramsSupport extends HologramManager {

    private final Map<String, Hologram> holograms = new HashMap<>();

    private final de.oliver.fancyholograms.api.HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    @Override
    public void createHologram(Location location, Crate crate) {
        if (crate.getCrateType() == CrateType.menu) return;

        CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) return;

        DisplayHologramData displayData = DisplayHologramData.getDefault(location.clone().add(getVector(crate))).setBillboard(Display.Billboard.CENTER).setVisibilityDistance(crateHologram.getRange());

        TextHologramData textData = TextHologramData.getDefault(name());
        textData.setText(crateHologram.getMessages());

        HologramData hologramData = new HologramData(name(), displayData, HologramType.TEXT, textData);

        Hologram hologram = this.manager.create(hologramData);

        this.manager.addHologram(hologram);

        new FoliaRunnable(this.plugin.getServer().getAsyncScheduler(), TimeUnit.SECONDS) {
            @Override
            public void run() {
                hologram.showHologram(plugin.getServer().getOnlinePlayers());
            }
        }.run(this.plugin);

        this.holograms.put(MiscUtils.location(location), hologram);
        this.manager.saveHolograms();
    }

    @Override
    public void removeHologram(Location location) {
        Hologram hologram = this.holograms.remove(MiscUtils.location(location));

        if (hologram != null) {
            this.manager.removeHologram(hologram);
            this.manager.saveHolograms();
        }
    }

    @Override
    public void removeAllHolograms() {
        this.holograms.forEach((key, value) -> this.manager.removeHologram(value));
        this.holograms.clear();

        this.manager.reloadHolograms();
    }

    @Override
    public boolean isEmpty() {
        return this.holograms.isEmpty();
    }
}