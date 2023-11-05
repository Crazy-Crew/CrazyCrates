package us.crazycrew.crazycrates.paper.api.crates.menus;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;

public abstract class InventoryBuilder implements InventoryHolder {

    private final CrazyCrates plugin;
    private final Inventory inventory;
    private final Player player;
    private String title;
    private Crate crate;
    private int size;
    private int page;

    public InventoryBuilder(CrazyCrates plugin, Player player, int size, String title) {
        this.plugin = plugin;
        this.title = title;
        this.player = player;
        this.size = size;

        this.inventory = plugin.getServer().createInventory(this, this.size, MsgUtils.color(this.title));
    }

    public InventoryBuilder(CrazyCrates plugin, Crate crate, Player player, int size, int page, String title) {
        this.plugin = plugin;
        this.title = title;
        this.player = player;
        this.size = size;
        this.page = page;

        this.crate = crate;

        this.inventory = plugin.getServer().createInventory(this, this.size, MsgUtils.color(this.title));
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

    public Player getPlayer() {
        return this.player;
    }

    public void update() {
        this.player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);
        this.player.openInventory(getInventory());
    }

    @NotNull
    public CrazyCrates getPlugin() {
        return this.plugin;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return this.inventory;
    }
}