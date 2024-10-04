package com.ryderbelserion.crazycrates.managers;

import com.ryderbelserion.crazycrates.api.holograms.TextHologram;
import org.bukkit.entity.TextDisplay;
import java.util.HashMap;
import java.util.Map;

public class DisplayManager {

    private static final Map<String, TextHologram> displays = new HashMap<>();

    public static void addDisplay(final String hologram, final TextHologram display) {
        displays.put(hologram, display);
    }

    public static void removeDisplay(final String name) {
        final TextHologram textHologram = displays.remove(name);

        if (textHologram == null) return;

        final TextDisplay display = textHologram.getTextDisplay();

        if (display == null) return;

        display.remove();
    }

    public static TextHologram getDisplay(final String hologram) {
        return displays.get(hologram);
    }

    public static Map<String, TextHologram> getDisplays() {
        return displays;
    }
}