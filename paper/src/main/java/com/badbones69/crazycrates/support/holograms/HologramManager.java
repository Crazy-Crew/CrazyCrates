package com.badbones69.crazycrates.support.holograms;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.crates.CrateHologram;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public abstract class HologramManager {

    protected CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    
    public abstract void createHologram(Location location, Crate crate);

    public abstract void removeHologram(Location location);

    public abstract void removeAllHolograms(boolean isShutdown);

    public abstract boolean isEmpty();

    protected @NotNull final String name() {
        return this.plugin.getName().toLowerCase() + "-" + UUID.randomUUID();
    }

    protected @NotNull final Vector getVector(@NotNull final Crate crate) {
        return new Vector(0.5, crate.getHologram().getHeight(), 0.5);
    }

    protected @Nullable final String color(@NotNull final String message) {
        if (message.isEmpty()) return null;

        final Matcher matcher = Pattern.compile("#[a-fA-F\\d]{6}").matcher(message);
        final StringBuilder buffer = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    protected @NotNull final List<String> lines(@NotNull final CrateHologram crateHologram) {
        if (crateHologram.getMessages().isEmpty()) return Collections.emptyList();

        final List<String> lines = new ArrayList<>();

        crateHologram.getMessages().forEach(line -> lines.add(color(line)));

        return lines;
    }
}