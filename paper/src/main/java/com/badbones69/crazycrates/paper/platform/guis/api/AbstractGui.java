package com.badbones69.crazycrates.paper.platform.guis.api;

import ch.jalu.configme.SettingsManager;
import com.badbones69.common.config.ConfigManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractGui {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final BukkitUserManager userManager = this.plugin.getUserManager();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final SettingsManager config = ConfigManager.getConfig();

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final Player player;
    protected final Crate crate;
    protected final int size;
    protected String title;

    public AbstractGui(@NotNull final Player player, @Nullable final Crate crate, @NotNull final String title, final int size) {
        this.player = player;
        this.crate = crate;
        this.title = title;
        this.size = size;
    }

    public boolean hasKey(@NotNull final String value) {
        return this.title.contains(value);
    }

    public void setTitle(@NotNull final String title) {
        this.title = title;
    }

    public @NotNull final String getPlaceholders(@NotNull final String line) {
        if (line.isEmpty()) return "";

        final UUID uuid = this.player.getUniqueId();
        final String name = this.player.getName();

        String message = line;

        for (final Crate crate : this.crateManager.getUsableCrates()) {
            final String fileName = crate.getFileName();
            final String crateName = fileName.toLowerCase();

            final int virtual = this.userManager.getVirtualKeys(uuid, fileName);
            final int physical = this.userManager.getPhysicalKeys(uuid, fileName);

            final int total = virtual + physical;

            final int opened = this.userManager.getCrateOpened(uuid, fileName);

            message = this.fusion.replacePlaceholders(line, Map.of(
                    "%{}%".replace("{}", crateName), StringUtils.formatNumber(virtual),
                    "%{}_physical%".replace("{}", crateName), StringUtils.format(physical),
                    "%{}_total%", StringUtils.format(total),
                    "%{}_opened%", StringUtils.format(opened),
                    "%{}_raw%", String.valueOf(virtual),
                    "%{}_raw_physical", String.valueOf(physical),
                    "%{}_raw_total%", String.valueOf(total),
                    "%{}_raw_opened%", String.valueOf(opened),

                    "{player}", name
            ));
        }

        return message;
    }

    public abstract void build();
}