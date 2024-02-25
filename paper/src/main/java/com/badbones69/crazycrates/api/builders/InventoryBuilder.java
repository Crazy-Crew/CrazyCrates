package com.badbones69.crazycrates.api.builders;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.support.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.List;

@SuppressWarnings("ALL")
public abstract class InventoryBuilder implements InventoryHolder {

    @NotNull
    protected final CrazyCrates plugin = CrazyCrates.get();

    private final Inventory inventory;
    private final Player player;
    private String title;
    private Crate crate;
    private int size;
    private int page;
    private List<Tier> tiers;

    public InventoryBuilder(Player player, int size, String title) {
        this.title = title;
        this.player = player;
        this.size = size;

        String inventoryTitle = MiscUtils.isPapiActive() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public InventoryBuilder(Crate crate, Player player, int size, String title) {
        this.title = title;
        this.player = player;
        this.size = size;

        this.crate = crate;

        String inventoryTitle = MiscUtils.isPapiActive() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public InventoryBuilder(Crate crate, Player player, int size, int page, String title) {
        this.title = title;
        this.player = player;
        this.size = size;
        this.page = page;

        this.crate = crate;

        String inventoryTitle = MiscUtils.isPapiActive() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public InventoryBuilder(List<Tier> tiers, Crate crate, Player player, int size, String title) {
        this.title = title;
        this.player = player;
        this.size = size;

        this.crate = crate;

        this.tiers = tiers;

        String inventoryTitle = MiscUtils.isPapiActive() ? PlaceholderAPI.setPlaceholders(getPlayer(), MsgUtils.color(this.title)) : MsgUtils.color(this.title);

        this.inventory = this.plugin.getServer().createInventory(this, this.size, inventoryTitle);
    }

    public abstract InventoryBuilder build();

    public void size(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public Crate getCrate() {
        return this.crate;
    }

    public void title(String title) {
        this.title = title;
    }

    public boolean contains(String message) {
        return this.title.contains(message);
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Tier> getTiers() {
        return this.tiers;
    }

    public InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
}