package com.badbones69.crazycrates.support.holograms.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.support.holograms.HologramManager;
import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.data.DisplayHologramData;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.Optional;

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

        final String format = "crazycrates-" + id;

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

        DisplayHologramData hologramData = new TextHologramData(format, location.clone().add(getVector(crate))).setBackground(background);

        final Hologram hologram = this.manager.create(hologramData);

        hologram.createHologram();

        this.plugin.getServer().getOnlinePlayers().forEach(hologram::updateShownStateFor);

        this.manager.addHologram(hologram);
    }

    @Override
    public void removeHologram(final String id) {
        final Optional<Hologram> hologram = this.manager.getHologram(this.plugin.getName().toLowerCase() + "-" + id);

        hologram.ifPresent(action -> {
            // Hide it.
            this.plugin.getServer().getOnlinePlayers().forEach(action::forceHideHologram);

            // Delete the hologram.
            action.deleteHologram();

            // Remove from cache.
            this.manager.removeHologram(action);
        });
    }

    @Override
    public void purge(final boolean isShutdown) {
        this.manager.getHolograms().forEach(hologram -> {
            if (hologram.getName().startsWith(this.plugin.getName().toLowerCase() + "-")) {
                // Hide it if not shutdown.
                if (!isShutdown) {
                    this.plugin.getServer().getOnlinePlayers().forEach(hologram::forceHideHologram);
                }

                // Delete the hologram.
                hologram.deleteHologram();

                // Remove from cache.
                this.manager.removeHologram(hologram);
            }
        });
    }
}