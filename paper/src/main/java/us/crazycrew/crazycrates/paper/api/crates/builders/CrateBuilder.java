package us.crazycrew.crazycrates.paper.api.crates.builders;

import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.menus.types.CratePrizeMenu;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;

public abstract class CrateBuilder {

    @NotNull
    protected final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

    private final InventoryBuilder menu;
    private final Player player;
    private final String title;
    private final Crate crate;
    private final int size;

    private Inventory inventory;

    protected CrateBuilder(Crate crate, Player player, int size, String title) {
        this.crate = crate;

        this.player = player;
        this.size = size;
        this.title = title;

        this.menu = new CratePrizeMenu(crate, player, 27, title);

        if (this.inventory == null) this.inventory = this.menu.build().getInventory();
    }

    public abstract void open(KeyType type, boolean checkHand);

    public void addCrateTask(Player player, BukkitTask task) {
        this.plugin.getCrateManager().addCrateTask(player, task);
    }

    public boolean hasCrateTask(Player player) {
        return this.plugin.getCrateManager().hasCrateTask(player);
    }

    public Crate getCrateTask(Player player) {
        return this.plugin.getCrateManager().getOpeningCrate(player);
    }

    public void removeCrateTask(Player player) {
        this.plugin.getCrateManager().removeCrateTask(player);
    }

    public Crate getCrate() {
        return this.crate;
    }

    public String getTitle() {
        return this.title;
    }

    public Player getPlayer() {
        return this.player;
    }

    public int getSize() {
        return this.size;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public InventoryBuilder getMenu() {
        return this.menu.build();
    }

    // Item Management
    public void setGlassPane(ItemStack item, int index) {
        getInventory().setItem(index, item);
    }

    public void setCustomGlassPane(int index) {
        ItemStack item = MiscUtils.getRandomPaneColor().setName(" ").build();
        getInventory().setItem(index, item);
    }
}