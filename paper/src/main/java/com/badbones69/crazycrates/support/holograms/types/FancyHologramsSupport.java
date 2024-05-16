package com.badbones69.crazycrates.support.holograms.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.ryderbelserion.vital.util.scheduler.FoliaRunnable;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.Hologram;
import de.oliver.fancyholograms.api.HologramType;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Display;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.Map;
import java.util.HashMap;

public class FancyHologramsSupport extends HologramManager {

    private final Map<String, Hologram> holograms = new HashMap<>();

    private final de.oliver.fancyholograms.api.HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

    @Override
    public void createHologram(@NotNull final Location location, @NotNull final Crate crate) {
        if (crate.getCrateType() == CrateType.menu) return;

        final CrateHologram crateHologram = crate.getHologram();

        if (!crateHologram.isEnabled()) {
            removeHologram(location);

            return;
        }

        final DisplayHologramData displayData = DisplayHologramData.getDefault(location.clone().add(getVector(crate)))
                .setBillboard(Display.Billboard.CENTER)
                .setVisibilityDistance(crateHologram.getRange());

        final String uuid = name();

        final TextHologramData textData = TextHologramData.getDefault(uuid);
        textData.setBackground(TextColor.fromCSSHexString(crateHologram.getBackgroundColor()));

        final String color = crateHologram.getBackgroundColor();

        if (color.equalsIgnoreCase("transparent")) {
            textData.setBackground(Hologram.TRANSPARENT);
        } else {
            textData.setBackground(TextColor.fromCSSHexString(color));
        }

        textData.setText(crateHologram.getMessages());

        final HologramData hologramData = new HologramData(uuid, displayData, HologramType.TEXT, textData);

        final Hologram hologram = this.manager.create(hologramData);

        this.manager.addHologram(hologram);
        this.manager.saveHolograms();

        final Server server = this.plugin.getServer();

        new FoliaRunnable(server.getAsyncScheduler(), null) {
            @Override
            public void run() {
                hologram.showHologram(server.getOnlinePlayers());
            }
        }.run(this.plugin);

        this.holograms.put(MiscUtils.location(location), hologram);
    }

    @Override
    public void removeHologram(@NotNull final Location location) {
        final Hologram hologram = this.holograms.remove(MiscUtils.location(location));

        if (hologram != null) {
            nuke(hologram, false);
        }
    }

    @Override
    public void removeAllHolograms(final boolean isShutdown) {
        if (!isEmpty()) {
            this.holograms.forEach((key, value) -> nuke(value, isShutdown));
            this.holograms.clear();
        }

        if (this.manager.getHolograms().isEmpty()) {
            this.manager.loadHolograms();
        }

        this.manager.getHolograms().forEach(hologram -> {
            final String name = hologram.getData().getName();

            if (name.startsWith(this.plugin.getName().toLowerCase() + "-")) {
                nuke(hologram, isShutdown);
            }
        });
    }

    private void nuke(@NotNull final Hologram hologram, final boolean isShutdown) {
        if (!isShutdown) {
            new FoliaRunnable(this.plugin.getServer().getAsyncScheduler(), null) {
                @Override
                public void run() {
                    // Hide the hologram first.
                    hologram.hideHologram(plugin.getServer().getOnlinePlayers());
                }
            }.run(this.plugin);
        }

        this.manager.removeHologram(hologram);
        this.manager.saveHolograms();
    }

    @Override
    public final boolean isEmpty() {
        return this.holograms.isEmpty();
    }
}