package com.badbones69.crazycrates.support.holograms.types;

import com.badbones69.crazycrates.api.crates.CrateHologram;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import com.ryderbelserion.vital.paper.util.scheduler.FoliaRunnable;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Server;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.ArrayList;
import java.util.List;

public class FancyHologramsSupport extends HologramManager {

    private final de.oliver.fancyholograms.api.HologramManager manager = FancyHologramsPlugin.get().getHologramManager();

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

        final String color = crateHologram.getBackgroundColor();

        final Color background;

        // taken from how fancyholograms handles colors
        if (color.equalsIgnoreCase("transparent")) {
            background = Hologram.TRANSPARENT;
        } else if (color.startsWith("#")) {
            Color parsed = Color.fromARGB((int) Long.parseLong(color.substring(1), 16));
            if (color.length() == 7) background = parsed.setAlpha(255); else background = parsed;
        } else {
            NamedTextColor textColor = NamedTextColor.NAMES.value(color.replace(' ', '_'));
            background = textColor == null ? null : Color.fromARGB(textColor.value() | 0xC8000000);
        }

        TextHologramData hologramData = new TextHologramData(name(id), location.clone().add(getVector(crate))).setBackground(background);

        hologramData.setText(crateHologram.getMessages());

        if (crateHologram.getUpdateInterval() != -1) {
            hologramData.setTextUpdateInterval(crateHologram.getUpdateInterval());
        }

        final Hologram hologram = this.manager.create(hologramData);

        hologram.createHologram();

        final Server server = this.plugin.getServer();

        new FoliaRunnable(server.getAsyncScheduler(), null) {
            @Override
            public void run() {
                server.getOnlinePlayers().forEach(hologram::updateShownStateFor);
            }
        }.run(this.plugin);

        this.manager.addHologram(hologram);
    }

    @Override
    public void removeHologram(final String id) {
        final Hologram hologram = this.manager.getHologram(name(id)).orElse(null);

        if (hologram == null) return;

        FancyHologramsPlugin.get().getHologramThread().submit(() -> this.manager.removeHologram(hologram));
    }

    @Override
    public boolean exists(final String id) {
        return this.manager.getHologram(name(id)).orElse(null) != null;
    }

    @Override
    public void purge(final boolean isShutdown) {
        final List<String> holograms = new ArrayList<>() {{
            manager.getHolograms().forEach(hologram -> {
                final String id = hologram.getName();

                if (id.startsWith(plugin.getName().toLowerCase() + "-")) {
                    add(id);
                }
            });
        }};

        holograms.forEach(this::removeHologram);
    }
}