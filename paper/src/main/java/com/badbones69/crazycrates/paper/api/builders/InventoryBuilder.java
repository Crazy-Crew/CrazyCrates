package com.badbones69.crazycrates.paper.api.builders;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.enums.other.Plugins;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.managers.InventoryManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.core.api.utils.AdvUtils;
import com.ryderbelserion.fusion.paper.utils.ColorUtils;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public abstract class InventoryBuilder implements InventoryHolder, Listener {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();
  
    protected final BukkitUserManager userManager = this.plugin.getUserManager();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final ComponentLogger logger = this.plugin.getComponentLogger();

    protected final Server server = this.plugin.getServer();

    private Inventory inventory;
    private Player player;
    private String title;
    private Crate crate;
    private int size;
    private int page;
    private List<Tier> tiers;

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size) {
        this.player = player;
        this.title = title;
        this.size = size;

        final String inventoryTitle = Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtils.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate) {
        this.player = player;
        this.title = title;
        this.size = size;

        this.crate = crate;

        final String inventoryTitle = Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtils.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, final int page, @NotNull final Crate crate) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.page = page;

        this.crate = crate;

        final String inventoryTitle = Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtils.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate, @NotNull final List<Tier> tiers) {
        this.player = player;
        this.title = title;
        this.size = size;

        this.crate = crate;
        this.tiers = tiers;

        final String inventoryTitle = Plugins.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtils.parse(inventoryTitle));
    }

    public InventoryBuilder() {}

    public abstract InventoryBuilder build();

    public abstract void run(InventoryClickEvent event);

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        run(event);
    }

    public void size(final int size) {
        this.size = size;
    }

    public final int getSize() {
        return this.size;
    }

    public final int getSize(final boolean isBorderEnabled) {
        return this.size - (isBorderEnabled ? 18 : this.size != 9 ? 9 : 0);
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public final int getPage() {
        return this.page;
    }

    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    public void title(@NotNull final String title) {
        this.title = title;
    }

    public final boolean contains(@NotNull final String message) {
        return this.title.contains(message);
    }

    public @NotNull final Player getPlayer() {
        return this.player;
    }

    public @NotNull final List<Tier> getTiers() {
        return this.tiers;
    }

    public @NotNull final InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    public void sendTitleChange() {
        ColorUtils.updateTitle(this.player, this.title);
    }

    @Override
    public @NotNull final Inventory getInventory() {
        return this.inventory;
    }
}