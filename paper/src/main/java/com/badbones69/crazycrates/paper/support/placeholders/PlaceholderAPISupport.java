package com.badbones69.crazycrates.paper.support.placeholders;

import com.badbones69.crazycrates.paper.support.placeholders.types.NamePlaceholder;
import com.badbones69.crazycrates.paper.support.placeholders.types.SimplePlaceholder;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.badbones69.crazycrates.paper.CrazyCrates;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import java.util.*;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    @Override
    public @NotNull final String onPlaceholderRequest(final Player player, @NotNull final String placeholder) {
        if (player == null || placeholder.isEmpty()) return "N/A";

        final UUID uuid = player.getUniqueId();

        switch (placeholder) {
            case "crates_opened_raw" -> {
                return String.valueOf(this.userManager.getTotalCratesOpened(uuid));
            }

            case "crates_opened" -> {
                return StringUtils.formatNumber(this.userManager.getTotalCratesOpened(uuid));
            }
        }

        return new SimplePlaceholder(uuid).get(placeholder).orElseGet(() -> new NamePlaceholder(player).get(placeholder).orElse("N/A"));
    }
    
    @Override
    public final boolean persist() {
        return true;
    }

    @Override
    public final boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull final String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    public @NotNull final String getAuthor() {
        return "ryderbelserion";
    }
    
    @Override
    public @NotNull final String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }
}